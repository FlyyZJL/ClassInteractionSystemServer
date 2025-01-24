package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@WebServlet("/addStudentsToCourse")
public class AddStudentsToCourseServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应类型
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 读取请求体中的 JSON 数据
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        // 解析 JSON 数据
        Gson gson = new Gson();
        String requestBody = jsonBuilder.toString();
        System.out.println("requestBody: " + requestBody);

        try {
            // 假设 JSON 包含 courseId 和 studentIds
            AddStudentsRequest requestData = gson.fromJson(requestBody, AddStudentsRequest.class);
            int courseId = requestData.getCourseId();
            List<Integer> studentIds = requestData.getStudentIds();

            // 调用 DAO 添加学生到课程
            StudentDao studentDao = new StudentDao();
            List<Integer> addedStudentIds = studentDao.addStudentsToCourse(courseId, studentIds);

            // 返回结果
            if (addedStudentIds.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"所有学生已在该课程中，无需添加\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"success\",\"message\":\"成功添加学生到课程\",\"addedStudentIds\":" + addedStudentIds + "}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"无效的请求数据\"}");
        }
    }

    // 内部类表示请求数据
    private static class AddStudentsRequest {
        private int courseId;
        private List<Integer> studentIds;

        public int getCourseId() {
            return courseId;
        }

        public List<Integer> getStudentIds() {
            return studentIds;
        }
    }
}

