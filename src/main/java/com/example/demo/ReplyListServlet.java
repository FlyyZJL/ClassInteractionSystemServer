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
import java.sql.SQLException;

@WebServlet("/api/replies/list")
public class ReplyListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //处理乱码
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=utf-8");

        try {
            int discussionId = Integer.parseInt(req.getParameter("discussion_id"));

            JsonArray replies = new JsonArray();
            String sql = "SELECT r.*, u.username as author_name " +
                    "FROM discussion_replies r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "WHERE r.discussion_id = ? " +
                    "ORDER BY COALESCE(parent_reply_id, reply_id), created_at";

            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, discussionId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject reply = new JsonObject();
                        reply.addProperty("id", rs.getInt("reply_id"));
                        reply.addProperty("content", rs.getString("content"));
                        reply.addProperty("author", rs.getString("author_name"));
                        reply.addProperty("created_at", rs.getTimestamp("created_at").getTime());
                        reply.addProperty("parent_reply_id", rs.getInt("parent_reply_id"));
                        replies.add(reply);
                    }
                }
                System.out.println(replies);
            }

            resp.getWriter().print(replies.toString());
        } catch (SQLException e) {
            resp.sendError(500, "数据库错误");
        }
    }
}