package com.example.demo;

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

@WebServlet("/api/teacher/submission-detail")
public class SubmissionDetailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 参数验证
            String submissionId = request.getParameter("submissionId");
            String teacherId = request.getParameter("teacherId");
            String role = request.getParameter("role");


            System.out.println("submissionId: " + submissionId + " teacherId: " + teacherId + " role: " + role);


            if (submissionId == null || teacherId == null || role == null) {
                response.setStatus(400);
                result.addProperty("message", "参数不完整");
                out.print(result.toString());
                return;
            }

            if (!"teacher".equals(role)) {
                response.setStatus(403);
                result.addProperty("message", "无访问权限");
                out.print(result.toString());
                return;
            }

            // 查询数据库
            String sql = "SELECT s.content, s.file_path, a.description, "
                    + "u.username, a.due_date, s.submit_time "
                    + "FROM assignment_submissions s "
                    + "JOIN assignments a ON s.assignment_id = a.assignment_id "
                    + "JOIN users u ON s.student_id = u.user_id "
                    + "WHERE s.submission_id = ?";

            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, Integer.parseInt(submissionId));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        JsonObject detail = new JsonObject();
                        detail.addProperty("content", rs.getString("content"));
                        detail.addProperty("filePath", rs.getString("file_path"));
                        detail.addProperty("studentName", rs.getString("username"));
                        detail.addProperty("dueDate", rs.getTimestamp("due_date").getTime());
                        detail.addProperty("submitTime", rs.getTimestamp("submit_time").getTime());
                        result.addProperty("success", true);
                        result.addProperty("message", "查询成功");
                        result.add("data", detail);

                        System.out.println("Response: " + result.toString());

                    } else {
                        response.setStatus(404);
                        result.addProperty("message", "未找到作业记录");
                    }
                }

            } catch (SQLException | NumberFormatException e) {
                response.setStatus(500);
                result.addProperty("message", "数据查询失败");
            }

        } catch (Exception e) {
            response.setStatus(500);
            result.addProperty("message", "服务器错误");
        } finally {
            out.print(result.toString());
        }
    }
}