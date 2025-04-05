package com.example.demo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmissionDAO {

    // 根据作业ID获取所有提交记录
    public List<AssignmentSubmission> getSubmissionsByAssignmentId(int assignmentId) throws SQLException, ClassNotFoundException {
        List<AssignmentSubmission> submissions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT s.*, u.username as student_name, a.title as assignment_title, " +
                    "g.username as grader_name " +
                    "FROM assignment_submissions s " +
                    "JOIN users u ON s.student_id = u.user_id " +
                    "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                    "LEFT JOIN users g ON s.graded_by = g.user_id " +
                    "WHERE s.assignment_id = ? " +
                    "ORDER BY s.submit_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, assignmentId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                AssignmentSubmission submission = new AssignmentSubmission();
                submission.setSubmissionId(rs.getInt("submission_id"));
                submission.setAssignmentId(rs.getInt("assignment_id"));
                submission.setStudentId(rs.getInt("student_id"));
                submission.setContent(rs.getString("content"));
                submission.setFilePath(rs.getString("file_path"));
                submission.setSubmitTime(rs.getTimestamp("submit_time"));

                // 处理可能为null的成绩和反馈
                double score = rs.getDouble("score");
                if (rs.wasNull()) {
                    submission.setScore(null);
                } else {
                    submission.setScore(score);
                }

                submission.setFeedback(rs.getString("feedback"));

                // 处理可能为null的评分人ID
                int gradedBy = rs.getInt("graded_by");
                if (rs.wasNull()) {
                    submission.setGradedBy(null);
                } else {
                    submission.setGradedBy(gradedBy);
                }

                submission.setGradedAt(rs.getTimestamp("graded_at"));

                // 设置扩展字段
                submission.setStudentName(rs.getString("student_name"));
                submission.setAssignmentTitle(rs.getString("assignment_title"));
                submission.setGradedByName(rs.getString("grader_name"));

                submissions.add(submission);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return submissions;
    }

    // 根据提交ID获取单个提交记录
    public AssignmentSubmission getSubmissionById(int submissionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        AssignmentSubmission submission = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT s.*, u.username as student_name, a.title as assignment_title, " +
                    "g.username as grader_name " +
                    "FROM assignment_submissions s " +
                    "JOIN users u ON s.student_id = u.user_id " +
                    "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                    "LEFT JOIN users g ON s.graded_by = g.user_id " +
                    "WHERE s.submission_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, submissionId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                submission = new AssignmentSubmission();
                submission.setSubmissionId(rs.getInt("submission_id"));
                submission.setAssignmentId(rs.getInt("assignment_id"));
                submission.setStudentId(rs.getInt("student_id"));
                submission.setContent(rs.getString("content"));
                submission.setFilePath(rs.getString("file_path"));
                submission.setSubmitTime(rs.getTimestamp("submit_time"));

                // 处理可能为null的成绩和反馈
                double score = rs.getDouble("score");
                if (rs.wasNull()) {
                    submission.setScore(null);
                } else {
                    submission.setScore(score);
                }

                submission.setFeedback(rs.getString("feedback"));

                // 处理可能为null的评分人ID
                int gradedBy = rs.getInt("graded_by");
                if (rs.wasNull()) {
                    submission.setGradedBy(null);
                } else {
                    submission.setGradedBy(gradedBy);
                }

                submission.setGradedAt(rs.getTimestamp("graded_at"));

                // 设置扩展字段
                submission.setStudentName(rs.getString("student_name"));
                submission.setAssignmentTitle(rs.getString("assignment_title"));
                submission.setGradedByName(rs.getString("grader_name"));
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return submission;
    }

    // 添加新提交记录
    public void addSubmission(AssignmentSubmission submission) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO assignment_submissions (assignment_id, student_id, content, file_path) " +
                    "VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, submission.getAssignmentId());
            stmt.setInt(2, submission.getStudentId());
            stmt.setString(3, submission.getContent());
            stmt.setString(4, submission.getFilePath());
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 更新提交记录
    public void updateSubmission(AssignmentSubmission submission) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE assignment_submissions SET content = ?, file_path = ? WHERE submission_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, submission.getContent());
            stmt.setString(2, submission.getFilePath());
            stmt.setInt(3, submission.getSubmissionId());
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 评分
    public void gradeSubmission(int submissionId, double score, String feedback, int gradedBy) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE assignment_submissions SET score = ?, feedback = ?, graded_by = ?, graded_at = CURRENT_TIMESTAMP " +
                    "WHERE submission_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, score);
            stmt.setString(2, feedback);
            stmt.setInt(3, gradedBy);
            stmt.setInt(4, submissionId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 检查学生是否已提交作业
    public boolean hasStudentSubmitted(int assignmentId, int studentId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean hasSubmitted = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM assignment_submissions WHERE assignment_id = ? AND student_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                hasSubmitted = true;
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return hasSubmitted;
    }

    // 删除提交记录
    public void deleteSubmission(int submissionId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM assignment_submissions WHERE submission_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, submissionId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 获取学生在课程中的所有提交记录
    public List<AssignmentSubmission> getStudentSubmissions(int courseId, int studentId) throws SQLException, ClassNotFoundException {
        List<AssignmentSubmission> submissions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT s.*, a.title as assignment_title, a.due_date, g.username as grader_name " +
                    "FROM assignment_submissions s " +
                    "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                    "LEFT JOIN users g ON s.graded_by = g.user_id " +
                    "WHERE a.course_id = ? AND s.student_id = ? " +
                    "ORDER BY s.submit_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            stmt.setInt(2, studentId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                AssignmentSubmission submission = new AssignmentSubmission();
                submission.setSubmissionId(rs.getInt("submission_id"));
                submission.setAssignmentId(rs.getInt("assignment_id"));
                submission.setStudentId(rs.getInt("student_id"));
                submission.setContent(rs.getString("content"));
                submission.setFilePath(rs.getString("file_path"));
                submission.setSubmitTime(rs.getTimestamp("submit_time"));

                // 处理可能为null的成绩和反馈
                double score = rs.getDouble("score");
                if (rs.wasNull()) {
                    submission.setScore(null);
                } else {
                    submission.setScore(score);
                }

                submission.setFeedback(rs.getString("feedback"));

                // 处理可能为null的评分人ID
                int gradedBy = rs.getInt("graded_by");
                if (rs.wasNull()) {
                    submission.setGradedBy(null);
                } else {
                    submission.setGradedBy(gradedBy);
                }

                submission.setGradedAt(rs.getTimestamp("graded_at"));

                // 设置扩展字段
                submission.setAssignmentTitle(rs.getString("assignment_title"));
                submission.setGradedByName(rs.getString("grader_name"));

                submissions.add(submission);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return submissions;
    }
}