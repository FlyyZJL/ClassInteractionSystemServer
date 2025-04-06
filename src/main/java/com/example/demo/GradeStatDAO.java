package com.example.demo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GradeStatDAO {

    /**
     * 获取课程成绩统计信息
     */
    public GradeStatistics getCourseStatistics(int courseId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // 获取课程平均分
            String avgSql = "SELECT AVG(score) as average_score FROM grades WHERE course_id = ?";
            stmt = conn.prepareStatement(avgSql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            double averageScore = 0;
            if (rs.next()) {
                averageScore = rs.getDouble("average_score");
            }

            rs.close();
            stmt.close();

            // 获取课程成绩分布
            String distSql = "SELECT " +
                    "SUM(CASE WHEN score >= 90 THEN 1 ELSE 0 END) as excellent, " +
                    "SUM(CASE WHEN score >= 80 AND score < 90 THEN 1 ELSE 0 END) as good, " +
                    "SUM(CASE WHEN score >= 70 AND score < 80 THEN 1 ELSE 0 END) as fair, " +
                    "SUM(CASE WHEN score >= 60 AND score < 70 THEN 1 ELSE 0 END) as pass, " +
                    "SUM(CASE WHEN score < 60 THEN 1 ELSE 0 END) as fail " +
                    "FROM grades WHERE course_id = ?";
            stmt = conn.prepareStatement(distSql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            Map<String, Integer> distribution = new HashMap<>();
            if (rs.next()) {
                distribution.put("优秀(90-100)", rs.getInt("excellent"));
                distribution.put("良好(80-89)", rs.getInt("good"));
                distribution.put("中等(70-79)", rs.getInt("fair"));
                distribution.put("及格(60-69)", rs.getInt("pass"));
                distribution.put("不及格(<60)", rs.getInt("fail"));
            }

            // 获取课程总人数和通过率
            String countSql = "SELECT COUNT(*) as total, " +
                    "SUM(CASE WHEN score >= 60 THEN 1 ELSE 0 END) as passed " +
                    "FROM grades WHERE course_id = ?";
            stmt = conn.prepareStatement(countSql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            int total = 0;
            int passed = 0;
            double passRate = 0;

            if (rs.next()) {
                total = rs.getInt("total");
                passed = rs.getInt("passed");
                passRate = total > 0 ? (double) passed / total * 100 : 0;
            }

            // 获取课程名称
            String nameSql = "SELECT course_name FROM courses WHERE course_id = ?";
            stmt = conn.prepareStatement(nameSql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            String courseName = "未知课程";
            if (rs.next()) {
                courseName = rs.getString("course_name");
            }

            // 创建并返回统计对象
            GradeStatistics stats = new GradeStatistics();
            stats.setCourseId(courseId);
            stats.setCourseName(courseName);
            stats.setAverageScore(averageScore);
            stats.setDistribution(distribution);
            stats.setTotalStudents(total);
            stats.setPassedStudents(passed);
            stats.setPassRate(passRate);

            return stats;

        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}