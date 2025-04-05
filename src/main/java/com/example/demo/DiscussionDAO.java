package com.example.demo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDAO {

    // 获取所有讨论（包含课程名和用户名）
    public List<Discussion> getAllDiscussions() throws SQLException, ClassNotFoundException {
        List<Discussion> discussions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT d.*, u.username, c.course_name, " +
                    "(SELECT COUNT(*) FROM discussion_replies r WHERE r.discussion_id = d.discussion_id AND r.is_deleted = 0) as reply_count " +
                    "FROM discussions d " +
                    "JOIN users u ON d.user_id = u.user_id " +
                    "JOIN courses c ON d.course_id = c.course_id " +
                    "ORDER BY d.is_pinned DESC, d.pinned_at DESC, d.created_at DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Discussion discussion = new Discussion();
                discussion.setDiscussionId(rs.getInt("discussion_id"));
                discussion.setCourseId(rs.getInt("course_id"));
                discussion.setUserId(rs.getInt("user_id"));
                discussion.setTitle(rs.getString("title"));
                discussion.setContent(rs.getString("content"));
                discussion.setCreatedAt(rs.getTimestamp("created_at"));
                discussion.setIsPinned(rs.getBoolean("is_pinned"));
                discussion.setPinnedAt(rs.getTimestamp("pinned_at"));
                discussion.setIsDeleted(rs.getBoolean("is_deleted"));
                discussion.setUsername(rs.getString("username"));
                discussion.setCourseName(rs.getString("course_name"));
                discussion.setReplyCount(rs.getInt("reply_count"));

                discussions.add(discussion);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return discussions;
    }

    // 根据课程ID获取讨论
    public List<Discussion> getDiscussionsByCourseId(int courseId) throws SQLException, ClassNotFoundException {
        List<Discussion> discussions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT d.*, u.username, c.course_name, " +
                    "(SELECT COUNT(*) FROM discussion_replies r WHERE r.discussion_id = d.discussion_id AND r.is_deleted = 0) as reply_count " +
                    "FROM discussions d " +
                    "JOIN users u ON d.user_id = u.user_id " +
                    "JOIN courses c ON d.course_id = c.course_id " +
                    "WHERE d.course_id = ? AND d.is_deleted = 0 " +
                    "ORDER BY d.is_pinned DESC, d.pinned_at DESC, d.created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Discussion discussion = new Discussion();
                discussion.setDiscussionId(rs.getInt("discussion_id"));
                discussion.setCourseId(rs.getInt("course_id"));
                discussion.setUserId(rs.getInt("user_id"));
                discussion.setTitle(rs.getString("title"));
                discussion.setContent(rs.getString("content"));
                discussion.setCreatedAt(rs.getTimestamp("created_at"));
                discussion.setIsPinned(rs.getBoolean("is_pinned"));
                discussion.setPinnedAt(rs.getTimestamp("pinned_at"));
                discussion.setIsDeleted(rs.getBoolean("is_deleted"));
                discussion.setUsername(rs.getString("username"));
                discussion.setCourseName(rs.getString("course_name"));
                discussion.setReplyCount(rs.getInt("reply_count"));

                discussions.add(discussion);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return discussions;
    }

    // 根据ID获取讨论
    public Discussion getDiscussionById(int discussionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Discussion discussion = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT d.*, u.username, c.course_name, " +
                    "(SELECT COUNT(*) FROM discussion_replies r WHERE r.discussion_id = d.discussion_id AND r.is_deleted = 0) as reply_count " +
                    "FROM discussions d " +
                    "JOIN users u ON d.user_id = u.user_id " +
                    "JOIN courses c ON d.course_id = c.course_id " +
                    "WHERE d.discussion_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussionId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                discussion = new Discussion();
                discussion.setDiscussionId(rs.getInt("discussion_id"));
                discussion.setCourseId(rs.getInt("course_id"));
                discussion.setUserId(rs.getInt("user_id"));
                discussion.setTitle(rs.getString("title"));
                discussion.setContent(rs.getString("content"));
                discussion.setCreatedAt(rs.getTimestamp("created_at"));
                discussion.setIsPinned(rs.getBoolean("is_pinned"));
                discussion.setPinnedAt(rs.getTimestamp("pinned_at"));
                discussion.setIsDeleted(rs.getBoolean("is_deleted"));
                discussion.setUsername(rs.getString("username"));
                discussion.setCourseName(rs.getString("course_name"));
                discussion.setReplyCount(rs.getInt("reply_count"));
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return discussion;
    }

    // 添加新讨论
    public void addDiscussion(Discussion discussion) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO discussions (course_id, user_id, title, content, is_pinned) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussion.getCourseId());
            stmt.setInt(2, discussion.getUserId());
            stmt.setString(3, discussion.getTitle());
            stmt.setString(4, discussion.getContent());
            stmt.setBoolean(5, discussion.getIsPinned());
            stmt.executeUpdate();

            // 如果设置了置顶，更新置顶时间
            if (discussion.getIsPinned()) {
                updatePinnedAt(conn, stmt, discussion.getDiscussionId());
            }
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 更新讨论
    public void updateDiscussion(Discussion discussion) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussions SET title = ?, content = ?, course_id = ?, is_pinned = ? WHERE discussion_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, discussion.getTitle());
            stmt.setString(2, discussion.getContent());
            stmt.setInt(3, discussion.getCourseId());
            stmt.setBoolean(4, discussion.getIsPinned());
            stmt.setInt(5, discussion.getDiscussionId());
            stmt.executeUpdate();

            // 处理置顶状态变更
            if (discussion.getIsPinned()) {
                // 检查是否需要更新置顶时间（如果当前未置顶）
                Discussion oldDiscussion = getDiscussionById(discussion.getDiscussionId());
                if (oldDiscussion != null && !oldDiscussion.getIsPinned()) {
                    updatePinnedAt(conn, stmt, discussion.getDiscussionId());
                }
            }
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 置顶讨论
    public void pinDiscussion(int discussionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussions SET is_pinned = true, pinned_at = CURRENT_TIMESTAMP WHERE discussion_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussionId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 取消置顶讨论
    public void unpinDiscussion(int discussionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussions SET is_pinned = false, pinned_at = NULL WHERE discussion_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussionId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 删除讨论（软删除）
    public void deleteDiscussion(int discussionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussions SET is_deleted = true WHERE discussion_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussionId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 恢复已删除的讨论
    public void restoreDiscussion(int discussionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE discussions SET is_deleted = false WHERE discussion_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, discussionId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 更新置顶时间辅助方法
    private void updatePinnedAt(Connection conn, PreparedStatement stmt, int discussionId) throws SQLException {
        String pinSql = "UPDATE discussions SET pinned_at = CURRENT_TIMESTAMP WHERE discussion_id = ?";
        stmt = conn.prepareStatement(pinSql);
        stmt.setInt(1, discussionId);
        stmt.executeUpdate();
    }

    // 获取讨论统计信息
    public int getDiscussionCount() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM discussions";
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

    // 获取活跃讨论统计
    public int getActiveDiscussionCount() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM discussions WHERE is_deleted = false";
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