package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/login")  // 设置该Servlet的URL映射
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Step 1: 读取请求体中的 JSON 数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        // Step 2: 使用 Gson 解析 JSON 数据
        Gson gson = new Gson();
        User user = gson.fromJson(jsonBuilder.toString(), User.class);

        // 打印日志，查看解析的数据
        System.out.println("Received parameters:");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());

        // Step 3: 验证用户名和密码
        String sql = "SELECT user_id, user_type FROM users WHERE username = ? AND password = ?";
        Object[] params = { user.getUsername(), user.getPassword() };

        try {
            ResultSet resultSet = DatabaseUtils.executeQuery(sql, params);
            if (resultSet.next()) {
                // 登录成功，返回用户数据
                int userId = resultSet.getInt("user_id");
                String userType = resultSet.getString("user_type");

                // 创建或获取session
                HttpSession session = request.getSession(true);
                // 设置session属性
                session.setAttribute("user_id", userId);
                session.setAttribute("user_type", userType);
                session.setAttribute("username", user.getUsername());
                // 设置session超时时间（单位：秒）
                session.setMaxInactiveInterval(3600); // 1小时

                // 返回 JSON 数据
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\": \"success\", \"user_id\": " + userId + ", \"user_type\": \"" + userType + "\"}");
            } else {
                // 登录失败
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"status\": \"error\", \"message\": \"用户名或密码错误\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"数据库错误\"}");
        }
    }
}