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
import java.sql.ResultSet;
import java.sql.Timestamp;
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



            // 检查是否已批改
            try (Connection conn = DatabaseUtils.getConnection()) {
                String checkSql = "SELECT s.score, s.feedback, u.username AS grader, s.graded_at " +
                        "FROM assignment_submissions s " +
                        "LEFT JOIN users u ON s.graded_by = u.user_id " +
                        "WHERE s.assignment_id = ? AND s.student_id = ? " +
                        "ORDER BY s.submit_time DESC LIMIT 1";

                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, assignmentId);
                    checkStmt.setInt(2, studentId);

                    ResultSet rs = checkStmt.executeQuery();
                    // 修改检查SQL部分
                    // 修改检查已批改部分的代码
                    if (rs.next() && rs.getObject("score") != null) {
                        // 使用Gson构建JSON
                        JsonObject gradeInfo = new JsonObject();
                        gradeInfo.addProperty("score", rs.getBigDecimal("score"));
                        gradeInfo.addProperty("feedback", rs.getString("feedback") != null ? rs.getString("feedback") : "");

                        // 处理可能为null的批改人
                        String grader = rs.getString("grader");
                        gradeInfo.addProperty("gradedBy", grader != null ? grader : "系统批改");

                        // 处理时间戳
                        Timestamp gradedAt = rs.getTimestamp("graded_at");
                        gradeInfo.addProperty("gradedAt", gradedAt != null ? gradedAt.getTime() : 0L);

                        // 构建完整响应
                        JsonObject responseJson = new JsonObject();
                        responseJson.addProperty("success", false);
                        responseJson.addProperty("code", "ALREADY_GRADED");
                        responseJson.add("gradeInfo", gradeInfo);
                        // 在返回403前添加日志
                        System.out.println("返回已批改响应: " + gson.toJson(responseJson));
                        // 设置响应并立即返回
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().print(gson.toJson(responseJson)); // 使用Gson序列化
                        response.getWriter().flush();
                        return; // 必须立即返回
                    }
                }
            }

            try (Connection conn = DatabaseUtils.getConnection()) {
                // ========== 新增：检查是否已有提交 ==========
                String checkSql = "SELECT COUNT(*) AS submission_count " +
                        "FROM assignment_submissions " +
                        "WHERE assignment_id = ? AND student_id = ?";

                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, assignmentId);
                    checkStmt.setInt(2, studentId);

                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt("submission_count") > 0) {
                        JsonObject errorResponse = new JsonObject();
                        errorResponse.addProperty("success", false);
                        errorResponse.addProperty("code", "ALREADY_SUBMITTED");
                        errorResponse.addProperty("message", "不可重复提交作业");

                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().print(gson.toJson(errorResponse));
                        return;
                    }
                }
            }

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