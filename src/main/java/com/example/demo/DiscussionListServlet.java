package com.example.demo;

import com.google.gson.JsonArray;
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

@WebServlet("/api/discussions")
public class DiscussionListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int courseId = Integer.parseInt(req.getParameter("course_id"));
            int userId = Integer.parseInt(req.getParameter("user_id"));
            String userRole = req.getParameter("user_role");

            JsonArray discussions = new JsonArray();
            String sql = "SELECT d.*, u.username AS author_name, " +
                    "(SELECT COUNT(*) FROM discussion_replies r " +
                    "WHERE r.discussion_id = d.discussion_id) AS reply_count " +
                    "FROM discussions d " +
                    "JOIN users u ON d.user_id = u.user_id " +
                    "WHERE d.course_id = ? AND d.is_deleted = FALSE " +
                    "ORDER BY d.is_pinned DESC, d.pinned_at DESC, d.created_at DESC";

            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, courseId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", rs.getInt("discussion_id"));
                    item.addProperty("title", rs.getString("title"));
                    item.addProperty("content", rs.getString("content"));
                    item.addProperty("author", rs.getString("author_name"));
                    item.addProperty("user_id", rs.getInt("user_id"));
                    item.addProperty("created_at", rs.getTimestamp("created_at").getTime());
                    item.addProperty("is_pinned", rs.getBoolean("is_pinned"));
                    item.addProperty("pinned_at", rs.getTimestamp("pinned_at") != null ?
                            rs.getTimestamp("pinned_at").getTime() : 0);
                    item.addProperty("reply_count", rs.getInt("reply_count"));
                    discussions.add(item);
                }
            }

            resp.setContentType("application/json; charset=utf-8");
            resp.setCharacterEncoding("UTF-8");

            resp.getWriter().print(discussions.toString());
        } catch (Exception e) {
            resp.sendError(500, "Database error: " + e.getMessage());
        }
    }
}