package com.example.demo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    // 获取所有学生
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT user_id AS studentId, username AS studentName, email " +
                "FROM users WHERE user_type = 'student'";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("studentId"),
                        rs.getString("studentName"),
                        rs.getString("email")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // 将学生添加到课程
    public List<Integer> addStudentsToCourse(int courseId, List<Integer> studentIds) {
        List<Integer> addedStudentIds = new ArrayList<>();
        String checkSql = "SELECT 1 FROM course_students WHERE course_id = ? AND student_id = ?";
        String insertSql = "INSERT INTO course_students (course_id, student_id) VALUES (?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql);
             PreparedStatement insertPs = conn.prepareStatement(insertSql)) {

            for (int studentId : studentIds) {
                // 检查该学生是否已经在课程中
                checkPs.setInt(1, courseId);
                checkPs.setInt(2, studentId);
                ResultSet rs = checkPs.executeQuery();

                if (!rs.next()) {
                    // 如果学生没有加入该课程，则插入记录
                    insertPs.setInt(1, courseId);
                    insertPs.setInt(2, studentId);
                    insertPs.addBatch(); // 添加到批处理
                    addedStudentIds.add(studentId); // 记录成功添加的学生ID
                } else {
                    System.out.println("student:" + studentId + " already in " + courseId);
                }
            }

            // 执行批量插入
            insertPs.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addedStudentIds;
    }
}
