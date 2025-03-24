package com.example.demo;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/discussions/detail")
public class DiscussionDetailServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //处理乱码
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=utf-8");

        try {
            int discussionId = Integer.parseInt(req.getParameter("discussion_id"));
            int userId = Integer.parseInt(req.getParameter("user_id"));
            String userRole = req.getParameter("user_role");

            JsonObject result = new JsonObject();

            try (Connection conn = DatabaseUtils.getConnection()) {
                // 获取帖子详情
                String sql = "SELECT d.*, u.username as author_name, " +
                        "(SELECT COUNT(*) FROM discussion_replies WHERE discussion_id = ?) as reply_count " +
                        "FROM discussions d " +
                        "JOIN users u ON d.user_id = u.user_id " +
                        "WHERE d.discussion_id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, discussionId);
                    ps.setInt(2, discussionId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            JsonObject discussion = new JsonObject();
                            discussion.addProperty("id", rs.getInt("discussion_id"));
                            discussion.addProperty("title", rs.getString("title"));
                            discussion.addProperty("content", rs.getString("content"));
                            discussion.addProperty("author", rs.getString("author_name"));
                            discussion.addProperty("course_id", rs.getInt("course_id"));
                            discussion.addProperty("created_at", rs.getTimestamp("created_at").getTime());
                            discussion.addProperty("reply_count", rs.getInt("reply_count"));
                            result.add("discussion", discussion);
                        } else {
                            resp.sendError(404, "讨论帖不存在");
                            return;
                        }
                    }
                }

                // 权限验证
                int courseId = result.getAsJsonObject("discussion").get("course_id").getAsInt();
                if (!validateCourseMembership(userId, courseId, userRole)) {
                    resp.sendError(403, "无查看权限");
                    return;
                }

                resp.getWriter().print(result.toString());
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(500, "服务器错误");
        }
    }

    // 复用之前的权限验证方法
    // 权限验证
    private boolean validateCourseMembership(int userId, int courseId, String role) {
        final String sql = "teacher".equals(role) ?
                "SELECT 1 FROM courses WHERE course_id = ? AND teacher_id = ?" :
                "SELECT 1 FROM course_students WHERE course_id = ? AND student_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("数据库错误: " + e.getMessage());
            return false;
        }
    }
}