package com.example.demo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/teacher/pending-submissions")
public class PendingSubmissionsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 1. 获取并验证基础参数
            int teacherId = Integer.parseInt(request.getParameter("teacherId"));
            String role = request.getParameter("role");

            // 2. 身份验证
            if (!"teacher".equals(role)) {
                sendError(response, result, out, HttpServletResponse.SC_FORBIDDEN, "无教师权限");
                return;
            }

            // 3. 数据库连接
            try (Connection conn = DatabaseUtils.getConnection()) {

                // 4. 验证教师有效性
                if (!isValidTeacher(conn, teacherId)) {
                    sendError(response, result, out, HttpServletResponse.SC_FORBIDDEN, "非法教师账号");
                    return;
                }

                // 5. 查询待批改作业
                String sql = "SELECT s.submission_id, s.assignment_id," // 新增assignment_id
                        + " u.username AS student_name, a.title AS assignment_title,"
                        + " c.course_name, s.submit_time," // 新增课程名称
                        + " (s.file_path IS NOT NULL) AS has_file "
                        + "FROM assignment_submissions s "
                        + "JOIN users u ON s.student_id = u.user_id "
                        + "JOIN assignments a ON s.assignment_id = a.assignment_id "
                        + "JOIN courses c ON a.course_id = c.course_id "
                        + "WHERE s.score IS NULL AND c.teacher_id = ? "
                        + "ORDER BY s.submit_time DESC"; // 添加排序


                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, teacherId);

                    try (ResultSet rs = ps.executeQuery()) {
                        JsonArray submissions = new JsonArray();
                        while (rs.next()) {
                            JsonObject sub = new JsonObject();
                            sub.addProperty("assignmentId", rs.getInt("assignment_id"));
                            sub.addProperty("courseName", rs.getString("course_name"));
                            sub.addProperty("submissionId", rs.getInt("submission_id"));
                            sub.addProperty("studentName", rs.getString("student_name"));
                            sub.addProperty("title", rs.getString("assignment_title"));
                            sub.addProperty("submitTime", rs.getTimestamp("submit_time").getTime());
                            sub.addProperty("hasFile", rs.getBoolean("has_file"));
                            submissions.add(sub);
                        }
                        result.add("data", submissions);
                        result.addProperty("success", true);
                    }
                }
            } catch (SQLException e) {
                handleSQLException(response, result, out, e);
            }
        } catch (NumberFormatException e) {
            sendError(response, result, out, HttpServletResponse.SC_BAD_REQUEST, "参数格式错误");
        } finally {
            out.print(result.toString());
        }
    }

    private boolean isValidTeacher(Connection conn, int teacherId) throws SQLException {
        String validationSql = "SELECT COUNT(*) FROM courses WHERE teacher_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(validationSql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void sendError(HttpServletResponse resp, JsonObject result, PrintWriter out,
                           int status, String message) {
        resp.setStatus(status);
        result.addProperty("message", message);
        result.addProperty("success", false);
        out.print(result.toString());
    }

    private void handleSQLException(HttpServletResponse resp, JsonObject result, PrintWriter out,
                                    SQLException e) {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        result.addProperty("message", "数据库错误: " + e.getMessage());
        result.addProperty("success", false);
        out.print(result.toString());
        e.printStackTrace();
    }
}