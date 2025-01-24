package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/student/changePassword")
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Step 1: 读取请求体中的 JSON 数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        // Step 2: 使用 Gson 解析请求中的 JSON 数据
        Gson gson = new Gson();
        ChangePasswordRequest changePasswordRequest = gson.fromJson(jsonBuilder.toString(), ChangePasswordRequest.class);

        String username = changePasswordRequest.getUsername();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        System.out.println("Received parameters:");
        System.out.println("Username: " + username);
        System.out.println("Old Password: " + oldPassword);
        System.out.println("New Password: " + newPassword);
        // Step 3: 校验参数
        if (username == null || oldPassword == null || newPassword == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"请求参数缺失\"}");
            return;
        }

        // Step 4: 查询旧密码是否正确
        String sql = "SELECT password FROM users WHERE username = ?";
        Object[] params = { username };

        try {
            ResultSet resultSet = DatabaseUtils.executeQuery(sql, params);
            if (resultSet.next()) {
                String currentPassword = resultSet.getString("password");

                // Step 5: 比较旧密码
                if (oldPassword.equals(currentPassword)) {
                    // Step 6: 更新为新密码
                    String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                    Object[] updateParams = { newPassword, username };
                    int rowsAffected = DatabaseUtils.executeUpdate(updateSql, updateParams);

                    if (rowsAffected > 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"success\", \"message\": \"密码修改成功\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("{\"status\": \"error\", \"message\": \"密码修改失败\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"status\": \"error\", \"message\": \"旧密码错误\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"status\": \"error\", \"message\": \"用户不存在\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"数据库错误\"}");
        }
    }

    // 用于接收前端传递的密码修改请求的请求体类
    private static class ChangePasswordRequest {
        private String username;
        private String oldPassword;
        private String newPassword;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
