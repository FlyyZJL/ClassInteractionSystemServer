package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.stream.Collectors;

@WebServlet("/api/student/assignments/submit")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class AssignmentSubmissionServlet extends HttpServlet {
    private static final Gson gson = new GsonBuilder().create();
    private static final String UPLOAD_DIR = "submissions";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 初始化参数
            String studentIdStr = null;
            String assignmentIdStr = null;
            String content = null;
            Part filePart = null;

            // 遍历所有Part
            for (Part part : request.getParts()) {
                String fieldName = part.getName();

                // 处理文件字段
                if ("file".equals(fieldName)) {
                    if (part.getSize() > 0) {
                        filePart = part;
                    }
                    continue;
                }

                // 处理文本字段
                switch (fieldName) {
                    case "studentId":
                        studentIdStr = getPartValue(part);
                        break;
                    case "assignmentId":
                        assignmentIdStr = getPartValue(part);
                        break;
                    case "content":
                        content = getPartValue(part);
                        break;
                }
            }

            System.out.println("studentId: " + studentIdStr+ " assignmentId: " + assignmentIdStr + " content: " + content);


            // 验证必填参数
            if (studentIdStr == null || assignmentIdStr == null) {
                throw new ServletException("缺少必要参数");
            }

            int studentId = Integer.parseInt(studentIdStr);
            int assignmentId = Integer.parseInt(assignmentIdStr);

            // 文件处理
            String fileName = null;
            if (filePart != null && filePart.getSize() > 0) {
                String appPath = getServletContext().getRealPath("");
                String uploadPath = appPath + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                fileName = String.format("%d_%d_%s",
                        studentId, assignmentId,
                        System.currentTimeMillis() + "_" + getFileName(filePart)
                );
                filePart.write(uploadPath + File.separator + fileName);
            }

            // 数据库操作
            String sql = "INSERT INTO assignment_submissions " +
                    "(assignment_id, student_id, content, file_path) " +
                    "VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, assignmentId);
                ps.setInt(2, studentId);
                ps.setString(3, content);
                ps.setString(4, fileName);

                int affectedRows = ps.executeUpdate();
                result.addProperty("success", affectedRows > 0);
                result.addProperty("message", affectedRows > 0 ? "提交成功" : "提交失败");
            }
        } catch (NumberFormatException e) {
            result.addProperty("success", false);
            result.addProperty("message", "参数格式错误: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "服务器错误: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        out.print(result.toString());
        out.flush();
    }



    // 获取普通字段的值
    private String getPartValue(Part part) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(part.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    // 获取文件名
    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        for (String headerPart : header.split(";")) {
            if (headerPart.trim().startsWith("filename")) {
                return headerPart.substring(
                        headerPart.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}