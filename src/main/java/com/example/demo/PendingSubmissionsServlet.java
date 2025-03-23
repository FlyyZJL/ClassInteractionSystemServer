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
            // 1. 权限验证
            int teacherId = Integer.parseInt(request.getParameter("teacherId"));
            String role = request.getParameter("role");

            if (!"teacher".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                result.addProperty("message", "无教师权限");
                out.print(result.toString());
                return;
            }

            // 2. 数据库查询
            String sql = "SELECT s.submission_id, u.username AS student_name, "
                    + "a.title AS assignment_title, s.submit_time, "
                    + "(s.file_path IS NOT NULL) AS has_file "
                    + "FROM assignment_submissions s "
                    + "JOIN users u ON s.student_id = u.user_id "
                    + "JOIN assignments a ON s.assignment_id = a.assignment_id "
                    + "WHERE s.score IS NULL";

            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                JsonArray submissions = new JsonArray();
                while (rs.next()) {
                    JsonObject sub = new JsonObject();
                    sub.addProperty("submissionId", rs.getInt("submission_id"));
                    sub.addProperty("studentName", rs.getString("student_name"));
                    sub.addProperty("title", rs.getString("assignment_title"));
                    sub.addProperty("submitTime", rs.getTimestamp("submit_time").getTime());
                    sub.addProperty("hasFile", rs.getBoolean("has_file"));
                    submissions.add(sub);
                }

                result.add("data", submissions);
                result.addProperty("success", true);

            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.addProperty("message", "数据库查询失败");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.addProperty("message", "参数格式错误");
        } finally {
            out.print(result.toString());
        }
    }
}