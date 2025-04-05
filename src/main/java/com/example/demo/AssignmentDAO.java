package com.example.demo;


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssignmentDAO {

    // 获取所有作业（包含课程名和提交统计）
    public List<Assignment> getAllAssignments() throws SQLException, ClassNotFoundException {
        List<Assignment> assignments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT a.*, c.course_name, " +
                    "(SELECT COUNT(*) FROM assignment_submissions s WHERE s.assignment_id = a.assignment_id) as submission_count, " +
                    "(SELECT COUNT(*) FROM course_students cs WHERE cs.course_id = a.course_id) as total_students " +
                    "FROM assignments a " +
                    "JOIN courses c ON a.course_id = c.course_id " +
                    "ORDER BY a.due_date ASC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            Date now = new Date();

            while (rs.next()) {
                Assignment assignment = new Assignment();
                assignment.setAssignmentId(rs.getInt("assignment_id"));
                assignment.setCourseId(rs.getInt("course_id"));
                assignment.setTitle(rs.getString("title"));
                assignment.setDescription(rs.getString("description"));

                Timestamp dueDate = rs.getTimestamp("due_date");
                assignment.setDueDate(dueDate);
                assignment.setIsOverdue(dueDate != null && dueDate.before(now));

                assignment.setCreatedAt(rs.getTimestamp("created_at"));
                assignment.setCourseName(rs.getString("course_name"));
                assignment.setSubmissionCount(rs.getInt("submission_count"));
                assignment.setTotalStudents(rs.getInt("total_students"));

                assignments.add(assignment);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return assignments;
    }

    // 根据课程ID获取作业
    public List<Assignment> getAssignmentsByCourseId(int courseId) throws SQLException, ClassNotFoundException {
        List<Assignment> assignments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT a.*, c.course_name, " +
                    "(SELECT COUNT(*) FROM assignment_submissions s WHERE s.assignment_id = a.assignment_id) as submission_count, " +
                    "(SELECT COUNT(*) FROM course_students cs WHERE cs.course_id = a.course_id) as total_students " +
                    "FROM assignments a " +
                    "JOIN courses c ON a.course_id = c.course_id " +
                    "WHERE a.course_id = ? " +
                    "ORDER BY a.due_date ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            Date now = new Date();

            while (rs.next()) {
                Assignment assignment = new Assignment();
                assignment.setAssignmentId(rs.getInt("assignment_id"));
                assignment.setCourseId(rs.getInt("course_id"));
                assignment.setTitle(rs.getString("title"));
                assignment.setDescription(rs.getString("description"));

                Timestamp dueDate = rs.getTimestamp("due_date");
                assignment.setDueDate(dueDate);
                assignment.setIsOverdue(dueDate != null && dueDate.before(now));

                assignment.setCreatedAt(rs.getTimestamp("created_at"));
                assignment.setCourseName(rs.getString("course_name"));
                assignment.setSubmissionCount(rs.getInt("submission_count"));
                assignment.setTotalStudents(rs.getInt("total_students"));

                assignments.add(assignment);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return assignments;
    }

    // 根据ID获取作业
    public Assignment getAssignmentById(int assignmentId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Assignment assignment = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT a.*, c.course_name, " +
                    "(SELECT COUNT(*) FROM assignment_submissions s WHERE s.assignment_id = a.assignment_id) as submission_count, " +
                    "(SELECT COUNT(*) FROM course_students cs WHERE cs.course_id = a.course_id) as total_students " +
                    "FROM assignments a " +
                    "JOIN courses c ON a.course_id = c.course_id " +
                    "WHERE a.assignment_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, assignmentId);
            rs = stmt.executeQuery();

            Date now = new Date();

            if (rs.next()) {
                assignment = new Assignment();
                assignment.setAssignmentId(rs.getInt("assignment_id"));
                assignment.setCourseId(rs.getInt("course_id"));
                assignment.setTitle(rs.getString("title"));
                assignment.setDescription(rs.getString("description"));

                Timestamp dueDate = rs.getTimestamp("due_date");
                assignment.setDueDate(dueDate);
                assignment.setIsOverdue(dueDate != null && dueDate.before(now));

                assignment.setCreatedAt(rs.getTimestamp("created_at"));
                assignment.setCourseName(rs.getString("course_name"));
                assignment.setSubmissionCount(rs.getInt("submission_count"));
                assignment.setTotalStudents(rs.getInt("total_students"));
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return assignment;
    }

    // 添加新作业
    public void addAssignment(Assignment assignment) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO assignments (course_id, title, description, due_date) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, assignment.getCourseId());
            stmt.setString(2, assignment.getTitle());
            stmt.setString(3, assignment.getDescription());
            stmt.setTimestamp(4, new Timestamp(assignment.getDueDate().getTime()));
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 更新作业
    public void updateAssignment(Assignment assignment) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE assignments SET course_id = ?, title = ?, description = ?, due_date = ? WHERE assignment_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, assignment.getCourseId());
            stmt.setString(2, assignment.getTitle());
            stmt.setString(3, assignment.getDescription());
            stmt.setTimestamp(4, new Timestamp(assignment.getDueDate().getTime()));
            stmt.setInt(5, assignment.getAssignmentId());
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 删除作业（同时删除相关提交记录）
    public void deleteAssignment(int assignmentId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 先删除作业的提交记录
            String deleteSubmissionsSql = "DELETE FROM assignment_submissions WHERE assignment_id = ?";
            stmt1 = conn.prepareStatement(deleteSubmissionsSql);
            stmt1.setInt(1, assignmentId);
            stmt1.executeUpdate();

            // 再删除作业本身
            String deleteAssignmentSql = "DELETE FROM assignments WHERE assignment_id = ?";
            stmt2 = conn.prepareStatement(deleteAssignmentSql);
            stmt2.setInt(1, assignmentId);
            stmt2.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw ex;
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
            DBConnection.close(stmt1, stmt2, conn);
        }
    }

    // 获取指定课程中即将截止的作业
    public List<Assignment> getUpcomingAssignments(int courseId, int daysAhead) throws SQLException, ClassNotFoundException {
        List<Assignment> assignments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT a.*, c.course_name FROM assignments a " +
                    "JOIN courses c ON a.course_id = c.course_id " +
                    "WHERE a.course_id = ? AND a.due_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? DAY) " +
                    "ORDER BY a.due_date ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            stmt.setInt(2, daysAhead);
            rs = stmt.executeQuery();

            Date now = new Date();

            while (rs.next()) {
                Assignment assignment = new Assignment();
                assignment.setAssignmentId(rs.getInt("assignment_id"));
                assignment.setCourseId(rs.getInt("course_id"));
                assignment.setTitle(rs.getString("title"));
                assignment.setDescription(rs.getString("description"));

                Timestamp dueDate = rs.getTimestamp("due_date");
                assignment.setDueDate(dueDate);
                assignment.setIsOverdue(dueDate != null && dueDate.before(now));

                assignment.setCreatedAt(rs.getTimestamp("created_at"));
                assignment.setCourseName(rs.getString("course_name"));

                assignments.add(assignment);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return assignments;
    }
}