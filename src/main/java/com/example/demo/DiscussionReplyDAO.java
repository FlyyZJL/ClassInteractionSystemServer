package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionReplyDAO {

    // 根据讨论ID获取所有回复
    public List<DiscussionReply> getRepliesByDiscussionId(int discussionId) throws SQLException, ClassNotFoundException {
        List<DiscussionReply> replies = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            // 查询语句使用左连接，以便能够获取到父回复为空的回复
            String sql = "SELECT r.*, u.username FROM discussion_replies r " +
                    "LEFT JOIN users u ON r.user_id = u.user_id " +
                    "WHERE r.discussion_id = ? " +
                    "ORDER BY r.created_at ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussionId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                DiscussionReply reply = new DiscussionReply();
                reply.setReplyId(rs.getInt("reply_id"));
                reply.setDiscussionId(rs.getInt("discussion_id"));
                reply.setUserId(rs.getInt("user_id"));

                // 处理可能为 null 的父回复ID
                int parentReplyId = rs.getInt("parent_reply_id");
                if (rs.wasNull()) {
                    reply.setParentReplyId(null);
                } else {
                    reply.setParentReplyId(parentReplyId);
                }

                reply.setContent(rs.getString("content"));
                reply.setIsDeleted(rs.getBoolean("is_deleted"));
                reply.setCreatedAt(rs.getTimestamp("created_at"));
                reply.setUsername(rs.getString("username"));

                replies.add(reply);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return replies;
    }

    // 根据回复ID获取回复
    public DiscussionReply getReplyById(int replyId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DiscussionReply reply = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.*, u.username FROM discussion_replies r " +
                    "LEFT JOIN users u ON r.user_id = u.user_id " +
                    "WHERE r.reply_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, replyId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                reply = new DiscussionReply();
                reply.setReplyId(rs.getInt("reply_id"));
                reply.setDiscussionId(rs.getInt("discussion_id"));
                reply.setUserId(rs.getInt("user_id"));

                // 处理可能为 null 的父回复ID
                int parentReplyId = rs.getInt("parent_reply_id");
                if (rs.wasNull()) {
                    reply.setParentReplyId(null);
                } else {
                    reply.setParentReplyId(parentReplyId);
                }

                reply.setContent(rs.getString("content"));
                reply.setIsDeleted(rs.getBoolean("is_deleted"));
                reply.setCreatedAt(rs.getTimestamp("created_at"));
                reply.setUsername(rs.getString("username"));
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return reply;
    }

    // 添加回复
    public void addReply(DiscussionReply reply) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO discussion_replies (discussion_id, user_id, parent_reply_id, content) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, reply.getDiscussionId());
            stmt.setInt(2, reply.getUserId());

            // 处理可能为null的父回复ID
            if (reply.getParentReplyId() == null) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, reply.getParentReplyId());
            }

            stmt.setString(4, reply.getContent());
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 更新回复
    public void updateReply(DiscussionReply reply) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussion_replies SET content = ? WHERE reply_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, reply.getContent());
            stmt.setInt(2, reply.getReplyId());
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 删除回复（软删除）
    public void deleteReply(int replyId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussion_replies SET is_deleted = true WHERE reply_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, replyId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 恢复删除的回复
    public void restoreReply(int replyId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussion_replies SET is_deleted = false WHERE reply_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, replyId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 获取讨论的回复数量
    public int getReplyCountByDiscussionId(int discussionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM discussion_replies WHERE discussion_id = ? AND is_deleted = false";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussionId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return count;
    }

    // 获取总回复数量
    public int getTotalReplyCount() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM discussion_replies";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return count;
    }
}