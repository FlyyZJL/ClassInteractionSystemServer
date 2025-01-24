package com.example.demo;



import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/getCoursesByTeacher")
public class GetCoursesByTeacherServlet extends HttpServlet {



    // 初始化 CourseDao
    private CourseDao courseDao = new CourseDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取教师ID
        String teacherIdParam = request.getParameter("teacherId");

        if (teacherIdParam == null || teacherIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"参数中需要教师ID\"}");
            return;
        }

        try {
            int teacherId = Integer.parseInt(teacherIdParam);

            // 获取教师的所有课程
            List<Course> courses = courseDao.getCoursesByTeacherId(teacherId);

            if (courses.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"未找到该教师的课程\"}");
                return;
            }

            // 将课程列表转换为JSON
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(courses);

            // 设置响应类型为 JSON
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(jsonResponse);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"无效的教师ID\"}");
        }
    }
}
