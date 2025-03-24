package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/api/discussions/manage")
public class DiscussionManageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int userId = Integer.parseInt(req.getParameter("user_id"));
            String userRole = req.getParameter("user_role");
            int discussionId = Integer.parseInt(req.getParameter("discussion_id"));
            String action = req.getParameter("action");

            try (Connection conn = DatabaseUtils.getConnection()) {
                switch (action) {
                    case "pin":
                        handlePinAction(conn, userId, userRole, discussionId);
                        break;
                    case "delete":
                        handleDeleteAction(conn, userId, userRole, discussionId);
                        break;
                    default:
                        resp.sendError(400, "Invalid action");
                        return;
                }
                resp.setStatus(200);
            }
        } catch (NumberFormatException e) {
            resp.sendError(400, "Invalid parameters");
        } catch (SQLException e) {
            resp.sendError(500, "Database error");
        }
    }

    private void handlePinAction(Connection conn, int userId, String userRole, int discussionId)
            throws SQLException {
        if (!"teacher".equals(userRole)) {
            throw new SQLException("Permission denied");
        }

        String sql = "UPDATE discussions SET is_pinned = NOT is_pinned, " +
                "pinned_at = CASE WHEN is_pinned THEN NULL ELSE NOW() END " +
                "WHERE discussion_id = ? AND course_id IN " +
                "(SELECT course_id FROM courses WHERE teacher_id = ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, discussionId);
            ps.setInt(2, userId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No discussion found");
            }
        }
    }

    private void handleDeleteAction(Connection conn, int userId, String userRole, int discussionId)
            throws SQLException {
        String sql = "UPDATE discussions SET is_deleted = TRUE " +
                "WHERE discussion_id = ? AND " +
                "(user_id = ? OR ? = 'teacher' AND course_id IN " +
                "(SELECT course_id FROM courses WHERE teacher_id = ?))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, discussionId);
            ps.setInt(2, userId);
            ps.setString(3, userRole);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }
}