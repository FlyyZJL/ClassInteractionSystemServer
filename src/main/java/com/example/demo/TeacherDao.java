package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDao {

    // 获取所有教师的方法
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            // SQL 查询语句
            String sql = "SELECT user_id, username, email FROM users WHERE user_type = 'teacher'";

            // 执行查询操作
            resultSet = DatabaseUtils.executeQuery(sql, null);

            // 遍历结果集
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeacherId(resultSet.getInt("user_id"));  // 获取用户ID
                teacher.setTeacherName(resultSet.getString("username"));  // 获取用户名
                teacher.setEmail(resultSet.getString("email"));  // 获取邮箱
                teachers.add(teacher);  // 将教师信息加入列表
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            DatabaseUtils.closeResources(null, null, resultSet);
        }
        return teachers;  // 返回教师列表
    }

    // 获取所有教师
    public List<User> getAllTeachers2() throws SQLException, ClassNotFoundException {
        List<User> teachers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE user_type = 'teacher' ORDER BY user_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User teacher = new User();
                teacher.setUserId(rs.getInt("user_id"));
                teacher.setUsername(rs.getString("username"));
                teacher.setEmail(rs.getString("email"));
                teacher.setUserType(rs.getString("user_type"));
                teacher.setCreatedAt(rs.getTimestamp("created_at"));
                teachers.add(teacher);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return teachers;
    }

    // 根据ID获取教师
    public User getTeacherById(int teacherId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User teacher = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE user_id = ? AND user_type = 'teacher'";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teacherId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                teacher = new User();
                teacher.setUserId(rs.getInt("user_id"));
                teacher.setUsername(rs.getString("username"));
                teacher.setEmail(rs.getString("email"));
                teacher.setUserType(rs.getString("user_type"));
                teacher.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return teacher;
    }
}
