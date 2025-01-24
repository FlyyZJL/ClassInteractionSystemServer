package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/getAllStudents")
public class GetAllStudentsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应类型为 JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 获取学生列表
        StudentDao studentDao = new StudentDao();
        List<Student> students = studentDao.getAllStudents();

        // 转换为 JSON 格式
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(students);

        // 写入响应
        response.getWriter().write(jsonResponse);
    }
}