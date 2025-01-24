package com.example.demo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    // 根据教师ID查询该教师所教授的所有课程
    public List<Course> getCoursesByTeacherId(int teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_name, c.description, u.username AS teacher_name " +
                "FROM courses c " +
                "JOIN users u ON c.teacher_id = u.user_id " +
                "WHERE c.teacher_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);  // 设置查询参数（教师ID）

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int courseId = rs.getInt("course_id");
                    String courseName = rs.getString("course_name");
                    String description = rs.getString("description");
                    String teacherName = rs.getString("teacher_name");

                    Course course = new Course(courseId, courseName, description, teacherId, teacherName);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }
}
