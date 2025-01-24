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
}
