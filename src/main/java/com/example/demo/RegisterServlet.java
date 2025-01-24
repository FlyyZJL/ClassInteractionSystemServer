package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/register")  // 设置该Servlet的URL映射
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        //解决乱码问题
        response.setContentType("application/json;charset=utf-8");

        // Step 1: 获取请求体中的 JSON 数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        // 读取 JSON 数据
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
        System.out.println("Email: " + user.getEmail());
        System.out.println("User Type: " + user.getUserType());

        // Step 3: 处理注册逻辑，检查参数是否完整
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null || user.getUserType() == null) {
            // 请求参数缺失，返回错误响应
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"请求参数缺失\"}");
            return;
        }

        // Step 4: 检查用户名是否已存在
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Object[] checkParams = {user.getUsername()}; // 获取 username
        try {
            Object result = DatabaseUtils.getSingleResult(checkUserSql, checkParams);
            if (result != null && ((Number) result).intValue() > 0) {
                // 用户名已存在
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"status\": \"error\", \"message\": \"用户名已存在\"}");
                return;
            }

            // Step 5: 插入新用户数据
            String insertUserSql = "INSERT INTO users (username, password, email, user_type) VALUES (?, ?, ?, ?)";
            Object[] params = {user.getUsername(), user.getPassword(), user.getEmail(), user.getUserType()};
            int rowsAffected = DatabaseUtils.executeUpdate(insertUserSql, params);

            if (rowsAffected > 0) {
                // 注册成功
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\": \"success\", \"message\": \"用户注册成功\"}");
            } else {
                // 注册失败
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\": \"error\", \"message\": \"注册失败\"}");
            }
        } catch (SQLException e) {
            // 数据库错误
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"数据库错误\"}");
        }
    }
}
