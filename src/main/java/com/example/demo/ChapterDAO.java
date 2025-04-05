package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChapterDAO {

    // 根据课程ID获取章节
    public List<Chapter2> getChaptersByCourseId(int courseId) throws SQLException, ClassNotFoundException {
        List<Chapter2> chapters = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM chapters WHERE course_id = ? ORDER BY id";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Chapter2 chapter = new Chapter2();
                chapter.setId(rs.getInt("id"));
                chapter.setCourseId(rs.getInt("course_id"));
                chapter.setTitle(rs.getString("title"));
                chapter.setContent(rs.getString("content"));
                chapter.setVideoUrl(rs.getString("video_url"));
                chapter.setCreatedAt(rs.getTimestamp("created_at"));
                chapters.add(chapter);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return chapters;
    }

    // 根据ID获取章节
    public Chapter2 getChapterById(int chapterId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Chapter2 chapter = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM chapters WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chapterId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                chapter = new Chapter2();
                chapter.setId(rs.getInt("id"));
                chapter.setCourseId(rs.getInt("course_id"));
                chapter.setTitle(rs.getString("title"));
                chapter.setContent(rs.getString("content"));
                chapter.setVideoUrl(rs.getString("video_url"));
                chapter.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return chapter;
    }

    // 添加章节
    public void addChapter(Chapter2 chapter) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO chapters (course_id, title, content, video_url) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chapter.getCourseId());
            stmt.setString(2, chapter.getTitle());
            stmt.setString(3, chapter.getContent());
            stmt.setString(4, chapter.getVideoUrl());
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // 更新章节
    public void updateChapter(Chapter2 chapter) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE chapters SET title = ?, content = ?, video_url = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, chapter.getTitle());
            stmt.setString(2, chapter.getContent());
            stmt.setString(3, chapter.getVideoUrl());
            stmt.setInt(4, chapter.getId());
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // 删除章节
    public void deleteChapter(int chapterId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM chapters WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chapterId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // 统计课程章节数
    public int countChaptersByCourseId(int courseId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM chapters WHERE course_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return count;
    }
}