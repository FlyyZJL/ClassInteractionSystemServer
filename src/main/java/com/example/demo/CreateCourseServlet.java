package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/createCourse")  // 设置该Servlet的URL映射
public class CreateCourseServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置字符编码为UTF-8，处理中文乱码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        // 获取请求中的课程信息
        String courseName = request.getParameter("course_name");
        String courseDescription = request.getParameter("course_description");
        String teacherIdParam = request.getParameter("teacher_id");

        System.out.println("courseName: " + courseName + " courseDescription: " + courseDescription + " teacherId: " + teacherIdParam);

        // 校验输入数据
        if (courseName == null || courseName.isEmpty() || teacherIdParam == null || teacherIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"课程名称和教师ID不能为空\"}");
            return;
        }

        int teacherId;
        try {
            teacherId = Integer.parseInt(teacherIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"无效的教师ID\"}");
            return;
        }

        // 创建课程对象，并保存到数据库
        Course course = new Course(courseName, courseDescription, teacherId);
        boolean isSuccess = saveCourseToDatabase(course);

        // 返回结果
        if (isSuccess) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\": \"success\", \"message\": \"课程创建成功\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"课程创建失败\"}");
        }
    }

    private boolean saveCourseToDatabase(Course course) {
        String sql = "INSERT INTO courses (course_name, description, teacher_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getDescription());
            ps.setInt(3, course.getTeacherId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;  // 如果插入成功，返回true
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}