package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet("/AssignmentSubmissionServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 1024 * 1024 * 10,       // 10 MB
        maxRequestSize = 1024 * 1024 * 100    // 100 MB
)
public class AssignmentSubmissionServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AssignmentSubmissionDAO submissionDAO;
    private Gson gson;
    private static final String UPLOAD_DIRECTORY = "assignment_uploads";

    public void init() {
        submissionDAO = new AssignmentSubmissionDAO();
        gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "getSubmissions":
                    getSubmissions(request, response);
                    break;
                case "getSubmission":
                    getSubmission(request, response);
                    break;
                case "downloadSubmission":
                    downloadSubmission(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "未指定有效操作");
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            handleError(response, e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "submitAssignment":
                    submitAssignment(request, response);
                    break;
                case "gradeSubmission":
                    gradeSubmission(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "未指定有效操作");
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            handleError(response, e);
        }
    }

    private void getSubmissions(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
        List<AssignmentSubmission> submissions = submissionDAO.getSubmissionsByAssignmentId(assignmentId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(submissions));
        out.flush();
    }

    private void getSubmission(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int submissionId = Integer.parseInt(request.getParameter("submissionId"));
        AssignmentSubmission submission = submissionDAO.getSubmissionById(submissionId);

        if (submission != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(submission));
            out.flush();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"提交记录不存在\"}");
        }
    }

    private void submitAssignment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException, ServletException {
        HttpSession session = request.getSession();
        int studentId = (Integer) session.getAttribute("user_id");
        int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
        String content = request.getParameter("content");

        // 检查是否已经提交过
        if (submissionDAO.hasStudentSubmitted(assignmentId, studentId)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"已经提交过此作业\"}");
            return;
        }

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setContent(content);

        // 处理文件上传
        Part filePart = request.getPart("file");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));

            // 创建唯一文件名
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // 确保上传目录存在
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            // 保存文件
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);

            // 设置文件路径
            submission.setFilePath(UPLOAD_DIRECTORY + "/" + uniqueFileName);
        }

        submissionDAO.addSubmission(submission);

        response.sendRedirect("student_assignment.jsp?id=" + assignmentId);
    }

    private void gradeSubmission(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int submissionId = Integer.parseInt(request.getParameter("submissionId"));
        double score = Double.parseDouble(request.getParameter("score"));
        String feedback = request.getParameter("feedback");

        // 获取当前登录的老师/管理员ID
        HttpSession session = request.getSession();
        int gradedBy = (Integer) session.getAttribute("user_id");

        submissionDAO.gradeSubmission(submissionId, score, feedback, gradedBy);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\": true}");
    }

    private void downloadSubmission(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int submissionId = Integer.parseInt(request.getParameter("submissionId"));
        AssignmentSubmission submission = submissionDAO.getSubmissionById(submissionId);
        response.setCharacterEncoding("UTF-8");

        if (submission != null && submission.getFilePath() != null) {
            // 构建正确的文件路径，确保包含submissions目录
            String contextPath = getServletContext().getRealPath("");

            // 检查filePath是否已经包含submissions目录
            String filePath;
            if (submission.getFilePath().startsWith("submissions/") ||
                    submission.getFilePath().startsWith("submissions\\")) {
                // 已包含submissions目录
                filePath = contextPath + File.separator + submission.getFilePath();
            } else {
                // 不包含submissions目录，需要添加
                filePath = contextPath + File.separator + "submissions" + File.separator + submission.getFilePath();
            }

            // 统一将路径中的双斜杠替换为单斜杠
            filePath = filePath.replace("\\\\", "\\").replace("//", "/");

            // 打印日志
            System.out.println("Original file path: " + contextPath + File.separator + submission.getFilePath());
            System.out.println("Fixed file path: " + filePath);
            System.out.println("Database file path: " + submission.getFilePath());

            File file = new File(filePath);

            if (file.exists()) {
                // 获取文件名
                String fileName;
                if (submission.getFilePath().contains("/")) {
                    fileName = submission.getFilePath().substring(submission.getFilePath().lastIndexOf("/") + 1);
                } else if (submission.getFilePath().contains("\\")) {
                    fileName = submission.getFilePath().substring(submission.getFilePath().lastIndexOf("\\") + 1);
                } else {
                    fileName = submission.getFilePath(); // 直接就是文件名
                }

                // 打印日志
                System.out.println("File name: " + fileName);
                System.out.println("File exists: " + file.exists());
                System.out.println("File size: " + file.length());

                // 设置响应头
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentType("application/octet-stream");
                response.setContentLength((int) file.length());

                // 将文件内容写入响应流
                java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
            } else {
                // 文件不存在，尝试直接在submissions目录中查找
                String submissionsDir = contextPath + File.separator + "submissions";
                File directory = new File(submissionsDir);

                // 获取文件名（不含路径）
                String targetFileName;
                if (submission.getFilePath().contains("/")) {
                    targetFileName = submission.getFilePath().substring(submission.getFilePath().lastIndexOf("/") + 1);
                } else if (submission.getFilePath().contains("\\")) {
                    targetFileName = submission.getFilePath().substring(submission.getFilePath().lastIndexOf("\\") + 1);
                } else {
                    targetFileName = submission.getFilePath();
                }

                System.out.println("Looking for file: " + targetFileName + " in directory: " + submissionsDir);

                // 在submissions目录中查找匹配的文件
                File[] matchingFiles = directory.listFiles((dir, name) -> name.equals(targetFileName));

                if (matchingFiles != null && matchingFiles.length > 0) {
                    File foundFile = matchingFiles[0];
                    System.out.println("Found matching file: " + foundFile.getAbsolutePath());

                    response.setHeader("Content-Disposition", "attachment; filename=\"" + targetFileName + "\"");
                    response.setContentType("application/octet-stream");
                    response.setContentLength((int) foundFile.length());
                    java.nio.file.Files.copy(foundFile.toPath(), response.getOutputStream());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("文件不存在 (已尝试的路径: " + filePath + " 和 " + submissionsDir + "/" + targetFileName + ")");
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("提交记录中不包含文件");
        }
    }
    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        PrintWriter out = response.getWriter();
        out.print("{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        out.flush();

        e.printStackTrace();
    }
}