package com.example.demo;

import com.example.demo.DatabaseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/student/info")
public class StudentInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");

        // 查询数据库，获取学生个人信息，包括 created_at 字段
        String sql = "SELECT username, email, user_type, created_at FROM users WHERE username = ?";
        Object[] params = { username };
        try {
            ResultSet resultSet = DatabaseUtils.executeQuery(sql, params);
            if (resultSet.next()) {
                // 返回学生信息
                String email = resultSet.getString("email");
                String userType = resultSet.getString("user_type");
                String createdAt = resultSet.getString("created_at");

                // 返回 JSON 数据
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"success\", \"username\": \"" + username + "\", \"email\": \"" + email + "\", \"user_type\": \"" + userType + "\", \"created_at\": \"" + createdAt + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"status\": \"error\", \"message\": \"学生信息未找到\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"数据库错误\"}");
        }
    }
}
