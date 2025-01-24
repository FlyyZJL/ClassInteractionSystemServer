package com.example.demo;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/getTeachers")
public class GetTeachersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型为 JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 调用 DAO 获取所有教师列表
        TeacherDao teacherDao = new TeacherDao();
        List<Teacher> teachers = teacherDao.getAllTeachers();

        // 使用 Gson 库将教师列表转换为 JSON 格式
        Gson gson = new Gson();
        String teachersJson = gson.toJson(teachers);

        System.out.println(teachersJson);

        // 输出 JSON 数据到响应体
        PrintWriter out = response.getWriter();
        out.print(teachersJson);
        out.flush();
    }
}
