package com.example.demo;

import com.example.demo.DatabaseUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/student/courses")
public class StudentCoursesServlet extends HttpServlet {

    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        //解决乱码问题
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();

        try {
            // 直接从请求参数获取学生ID（需要客户端传递）
            String studentId = request.getParameter("studentId");
            if (studentId == null || studentId.isEmpty()) {
                out.print(gson.toJson(new ApiResult(false, "缺少studentId参数", null)));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            List<Course> courses = getStudentCourses(Integer.parseInt(studentId));
            out.print(gson.toJson(new ApiResult(true, "成功", courses)));

        } catch (NumberFormatException e) {
            out.print(gson.toJson(new ApiResult(false, "参数格式错误", null)));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            out.print(gson.toJson(new ApiResult(false, "数据库错误", null)));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private List<Course> getStudentCourses(int studentId) throws SQLException {
        // 保持原有SQL查询逻辑
        String sql = "SELECT c.course_id, c.course_name, c.description, c.teacher_id, "
                + "u.username AS teacher_name, u.email AS teacher_email "
                + "FROM courses c "
                + "JOIN users u ON c.teacher_id = u.user_id "
                + "JOIN course_students cs ON c.course_id = cs.course_id "
                + "WHERE cs.student_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getInt("teacher_id"),
                        rs.getString("teacher_name")
                );
                course.setTeacherEmail(rs.getString("teacher_email")); // 需要添加字段
                courses.add(course);
            }
            return courses;
        }
    }

    // 统一响应结构
    private static class ApiResult<T> {
        boolean success;
        String message;
        T data;

        ApiResult(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }
}