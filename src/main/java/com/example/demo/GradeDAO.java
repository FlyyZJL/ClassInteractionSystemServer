package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    // 获取所有成绩记录（包含课程名和学生名）
    public List<Grade> getAllGrades() throws SQLException, ClassNotFoundException {
        List<Grade> grades = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT g.*, c.course_name, u.username as student_name, a.username as graded_by_name " +
                    "FROM grades g " +
                    "JOIN courses c ON g.course_id = c.course_id " +
                    "JOIN users u ON g.student_id = u.user_id " +
                    "LEFT JOIN users a ON g.graded_by = a.user_id " +
                    "ORDER BY g.grade_date DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Grade grade = extractGradeFromResultSet(rs);
                grades.add(grade);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return grades;
    }

    // 根据学生ID获取成绩记录
    public List<Grade> getGradesByStudentId(int studentId) throws SQLException, ClassNotFoundException {
        List<Grade> grades = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT g.*, c.course_name, u.username as student_name, a.username as graded_by_name " +
                    "FROM grades g " +
                    "JOIN courses c ON g.course_id = c.course_id " +
                    "JOIN users u ON g.student_id = u.user_id " +
                    "LEFT JOIN users a ON g.graded_by = a.user_id " +
                    "WHERE g.student_id = ? " +
                    "ORDER BY g.grade_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Grade grade = extractGradeFromResultSet(rs);
                grades.add(grade);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return grades;
    }

    // 根据课程ID获取成绩记录
    public List<Grade> getGradesByCourseId(int courseId) throws SQLException, ClassNotFoundException {
        List<Grade> grades = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT g.*, c.course_name, u.username as student_name, a.username as graded_by_name " +
                    "FROM grades g " +
                    "JOIN courses c ON g.course_id = c.course_id " +
                    "JOIN users u ON g.student_id = u.user_id " +
                    "LEFT JOIN users a ON g.graded_by = a.user_id " +
                    "WHERE g.course_id = ? " +
                    "ORDER BY g.student_id, g.grade_type";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Grade grade = extractGradeFromResultSet(rs);
                grades.add(grade);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return grades;
    }

    // 根据ID获取单个成绩记录
    public Grade getGradeById(int gradeId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Grade grade = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT g.*, c.course_name, u.username as student_name, a.username as graded_by_name " +
                    "FROM grades g " +
                    "JOIN courses c ON g.course_id = c.course_id " +
                    "JOIN users u ON g.student_id = u.user_id " +
                    "LEFT JOIN users a ON g.graded_by = a.user_id " +
                    "WHERE g.grade_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gradeId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                grade = extractGradeFromResultSet(rs);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return grade;
    }

    // 添加新成绩记录
    public void addGrade(Grade grade) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO grades (course_id, student_id, grade_type, score, feedback, graded_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, grade.getCourseId());
            stmt.setInt(2, grade.getStudentId());
            stmt.setString(3, grade.getGradeType());
            stmt.setDouble(4, grade.getScore());
            stmt.setString(5, grade.getFeedback());
            stmt.setInt(6, grade.getGradedBy());
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 更新成绩记录
    public void updateGrade(Grade grade) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE grades SET course_id = ?, student_id = ?, grade_type = ?, " +
                    "score = ?, feedback = ?, graded_by = ?, grade_date = CURRENT_TIMESTAMP " +
                    "WHERE grade_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, grade.getCourseId());
            stmt.setInt(2, grade.getStudentId());
            stmt.setString(3, grade.getGradeType());
            stmt.setDouble(4, grade.getScore());
            stmt.setString(5, grade.getFeedback());
            stmt.setInt(6, grade.getGradedBy());
            stmt.setInt(7, grade.getGradeId());
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 删除成绩记录
    public void deleteGrade(int gradeId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM grades WHERE grade_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gradeId);
            stmt.executeUpdate();
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    // 获取课程中的所有学生
    public List<User> getStudentsForCourse(int courseId) throws SQLException, ClassNotFoundException {
        List<User> students = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT u.user_id, u.username FROM course_students cs " +
                    "JOIN users u ON cs.student_id = u.user_id " +
                    "WHERE cs.course_id = ? AND u.user_type = 'student' " +
                    "ORDER BY u.username";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User student = new User();
                student.setUserId(rs.getInt("user_id"));
                student.setUsername(rs.getString("username"));
                students.add(student);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return students;
    }

    // 从ResultSet中提取Grade对象
    private Grade extractGradeFromResultSet(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setCourseId(rs.getInt("course_id"));
        grade.setStudentId(rs.getInt("student_id"));
        grade.setGradeType(rs.getString("grade_type"));
        grade.setScore(rs.getDouble("score"));
        grade.setFeedback(rs.getString("feedback"));
        grade.setGradeDate(rs.getTimestamp("grade_date"));
        grade.setGradedBy(rs.getInt("graded_by"));

        // 设置扩展字段
        grade.setStudentName(rs.getString("student_name"));
        grade.setCourseName(rs.getString("course_name"));
        grade.setGradedByName(rs.getString("graded_by_name"));

        return grade;
    }

    // 检查是否已存在相同类型的成绩记录
    public boolean gradeExists(int courseId, int studentId, String gradeType) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM grades WHERE course_id = ? AND student_id = ? AND grade_type = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            stmt.setInt(2, studentId);
            stmt.setString(3, gradeType);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return exists;
    }

    // 获取学生在课程中的所有成绩
    public List<Grade> getStudentCourseGrades(int courseId, int studentId) throws SQLException, ClassNotFoundException {
        List<Grade> grades = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT g.*, c.course_name, u.username as student_name, a.username as graded_by_name " +
                    "FROM grades g " +
                    "JOIN courses c ON g.course_id = c.course_id " +
                    "JOIN users u ON g.student_id = u.user_id " +
                    "LEFT JOIN users a ON g.graded_by = a.user_id " +
                    "WHERE g.course_id = ? AND g.student_id = ? " +
                    "ORDER BY g.grade_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            stmt.setInt(2, studentId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Grade grade = extractGradeFromResultSet(rs);
                grades.add(grade);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return grades;
    }

    // 获取所有符合条件的成绩（用于导出）
    public List<Grade> getGradesForExport(Integer courseId) throws SQLException, ClassNotFoundException {
        List<Grade> grades = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql;
            if (courseId == null) {
                sql = "SELECT g.*, c.course_name, u.username as student_name, a.username as graded_by_name " +
                        "FROM grades g " +
                        "JOIN courses c ON g.course_id = c.course_id " +
                        "JOIN users u ON g.student_id = u.user_id " +
                        "LEFT JOIN users a ON g.graded_by = a.user_id " +
                        "ORDER BY c.course_name, u.username, g.grade_type";
                stmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT g.*, c.course_name, u.username as student_name, a.username as graded_by_name " +
                        "FROM grades g " +
                        "JOIN courses c ON g.course_id = c.course_id " +
                        "JOIN users u ON g.student_id = u.user_id " +
                        "LEFT JOIN users a ON g.graded_by = a.user_id " +
                        "WHERE g.course_id = ? " +
                        "ORDER BY u.username, g.grade_type";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, courseId);
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                Grade grade = extractGradeFromResultSet(rs);
                grades.add(grade);
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }

        return grades;
    }
}