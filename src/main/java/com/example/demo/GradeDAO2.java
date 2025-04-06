package com.example.demo;


import java.util.Date;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO2 {

    /**
     * 添加成绩
     */
    public Grade addGrade(Grade grade) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO grades (course_id, student_id, grade_type, score, feedback, graded_by) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, grade.getCourseId());
            stmt.setInt(2, grade.getStudentId());
            stmt.setString(3, grade.getGradeType());
            stmt.setDouble(4, grade.getScore());
            stmt.setString(5, grade.getFeedback());
            stmt.setInt(6, grade.getGradedBy());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("添加成绩失败，没有行被插入。");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                grade.setGradeId(rs.getInt(1));
            } else {
                throw new SQLException("添加成绩失败，未获取到ID。");
            }

            return getGradeById(grade.getGradeId()); // 返回包含完整信息的成绩对象
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    /**
     * 更新成绩
     */
    public Grade updateGrade(Grade grade) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE grades SET course_id = ?, student_id = ?, grade_type = ?, "
                    + "score = ?, feedback = ?, graded_by = ? "
                    + "WHERE grade_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, grade.getCourseId());
            stmt.setInt(2, grade.getStudentId());
            stmt.setString(3, grade.getGradeType());
            stmt.setDouble(4, grade.getScore());
            stmt.setString(5, grade.getFeedback());
            stmt.setInt(6, grade.getGradedBy());
            stmt.setInt(7, grade.getGradeId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("更新成绩失败，未找到ID为 " + grade.getGradeId() + " 的成绩。");
            }

            return getGradeById(grade.getGradeId()); // 返回更新后的完整信息
        } finally {
            DBConnection.close((ResultSet) null, stmt, conn);
        }
    }

    /**
     * 删除成绩
     */
    public boolean deleteGrade(int gradeId, int teacherId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            // 检查教师是否有权限删除该成绩
            String checkSql = "SELECT g.grade_id FROM grades g "
                    + "JOIN courses c ON g.course_id = c.course_id "
                    + "WHERE g.grade_id = ? AND c.teacher_id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, gradeId);
            stmt.setInt(2, teacherId);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return false; // 没有权限或成绩不存在
            }

            // 有权限，执行删除
            stmt.close();
            String deleteSql = "DELETE FROM grades WHERE grade_id = ?";
            stmt = conn.prepareStatement(deleteSql);
            stmt.setInt(1, gradeId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DBConnection.close((ResultSet) null, stmt, conn);
        }
    }

    /**
     * 根据ID获取成绩详情
     */
    public Grade getGradeById(int gradeId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT g.*, c.course_name, s.username as student_name, t.username as graded_by_name "
                    + "FROM grades g "
                    + "JOIN courses c ON g.course_id = c.course_id "
                    + "JOIN users s ON g.student_id = s.user_id "
                    + "LEFT JOIN users t ON g.graded_by = t.user_id "  // 使用LEFT JOIN确保即使找不到批改人也能返回成绩
                    + "WHERE g.grade_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gradeId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                Grade grade = new Grade();
                grade.setGradeId(rs.getInt("grade_id"));
                grade.setCourseId(rs.getInt("course_id"));
                grade.setStudentId(rs.getInt("student_id"));
                grade.setGradeType(rs.getString("grade_type"));
                grade.setScore(rs.getDouble("score"));
                grade.setFeedback(rs.getString("feedback"));

                // 处理日期，确保即使数据库中为null也设置当前日期
                Timestamp gradeDate = rs.getTimestamp("grade_date");
                grade.setGradeDate(gradeDate != null ? gradeDate : new Date());

                grade.setGradedBy(rs.getInt("graded_by"));

                // 设置扩展字段
                grade.setCourseName(rs.getString("course_name"));
                grade.setStudentName(rs.getString("student_name"));

                // 处理批改人名称，确保不为null
                String gradedByName = rs.getString("graded_by_name");
                grade.setGradedByName(gradedByName != null ? gradedByName : "未知");
                //打印所有对象的日志在一行
                System.out.println("Grade Details: " + grade.toString());



                return grade;
            } else {
                return null;
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    /**
     * 获取课程的成绩列表
     */
    public List<Grade> getCourseGrades(int courseId, int teacherId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            // 先检查教师是否有权限查看这个课程的成绩
            String checkSql = "SELECT course_id FROM courses WHERE course_id = ? AND teacher_id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, courseId);
            stmt.setInt(2, teacherId);

            rs = stmt.executeQuery();
            if (!rs.next()) {
                return new ArrayList<>(); // 没有权限，返回空列表
            }

            // 有权限，获取成绩列表
            stmt.close();
            rs.close();

            String sql = "SELECT g.*, c.course_name, u.username as student_name, t.username as graded_by_name "
                    + "FROM grades g "
                    + "JOIN courses c ON g.course_id = c.course_id "
                    + "JOIN users u ON g.student_id = u.user_id "
                    + "LEFT JOIN users t ON g.graded_by = t.user_id "
                    + "WHERE g.course_id = ? "
                    + "ORDER BY g.grade_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);

            rs = stmt.executeQuery();
            List<Grade> grades = new ArrayList<>();
            while (rs.next()) {
                grades.add(extractGradeFromResultSet(rs));
            }

            return grades;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    /**
     * 获取教师的所有课程成绩（用于导出）
     */
    public List<Grade> getGradesForExport(int courseId, int teacherId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql;

            if (courseId > 0) {
                // 获取特定课程的成绩
                sql = "SELECT g.*, c.course_name, u.username as student_name, t.username as graded_by_name "
                        + "FROM grades g "
                        + "JOIN courses c ON g.course_id = c.course_id "
                        + "JOIN users u ON g.student_id = u.user_id "
                        + "LEFT JOIN users t ON g.graded_by = t.user_id "
                        + "WHERE g.course_id = ? AND c.teacher_id = ? "
                        + "ORDER BY u.username, g.grade_type";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, courseId);
                stmt.setInt(2, teacherId);
            } else {
                // 获取教师所有课程的成绩
                sql = "SELECT g.*, c.course_name, u.username as student_name, t.username as graded_by_name "
                        + "FROM grades g "
                        + "JOIN courses c ON g.course_id = c.course_id "
                        + "JOIN users u ON g.student_id = u.user_id "
                        + "LEFT JOIN users t ON g.graded_by = t.user_id "
                        + "WHERE c.teacher_id = ? "
                        + "ORDER BY c.course_name, u.username, g.grade_type";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, teacherId);
            }

            rs = stmt.executeQuery();
            List<Grade> grades = new ArrayList<>();
            while (rs.next()) {
                grades.add(extractGradeFromResultSet(rs));
            }

            return grades;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    /**
     * 检查成绩是否存在
     */
    public boolean gradeExists(int courseId, int studentId, String gradeType) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT grade_id FROM grades "
                    + "WHERE course_id = ? AND student_id = ? AND grade_type = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            stmt.setInt(2, studentId);
            stmt.setString(3, gradeType);

            rs = stmt.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    /**
     * 根据参数获取成绩记录
     */
    public Grade getGradeByParams(int courseId, int studentId, String gradeType) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT g.*, c.course_name, u.username as student_name, t.username as graded_by_name "
                    + "FROM grades g "
                    + "JOIN courses c ON g.course_id = c.course_id "
                    + "JOIN users u ON g.student_id = u.user_id "
                    + "LEFT JOIN users t ON g.graded_by = t.user_id "
                    + "WHERE g.course_id = ? AND g.student_id = ? AND g.grade_type = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            stmt.setInt(2, studentId);
            stmt.setString(3, gradeType);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return extractGradeFromResultSet(rs);
            } else {
                return null;
            }
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    /**
     * 从ResultSet提取Grade对象
     */
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
        grade.setCourseName(rs.getString("course_name"));
        grade.setStudentName(rs.getString("student_name"));
        grade.setGradedByName(rs.getString("graded_by_name"));

        return grade;
    }
}