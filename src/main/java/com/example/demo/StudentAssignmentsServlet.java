package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/student/assignments")
public class StudentAssignmentsServlet extends HttpServlet {
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss") // 统一日期格式
            .disableHtmlEscaping()
            .create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        //解决乱码问题
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 获取请求参数
            String studentId = request.getParameter("studentId");
            if (studentId == null || studentId.isEmpty()) {
                result.addProperty("success", false);
                result.addProperty("message", "缺少studentId参数");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(result);
                return;
            }

            List<Assignment> assignments = getStudentAssignments(Integer.parseInt(studentId));
            result.add("data", gson.toJsonTree(assignments));
            result.addProperty("success", true);

        } catch (NumberFormatException e) {
            result.addProperty("success", false);
            result.addProperty("message", "参数格式错误");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            result.addProperty("success", false);
            result.addProperty("message", "数据库错误");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        out.print(result);
        out.flush();
    }

    private List<Assignment> getStudentAssignments(int studentId) throws SQLException {
        String sql = "SELECT a.assignment_id AS id, a.title, a.description, "
                + "a.due_date, c.course_name, a.created_at "
                + "FROM assignments a "
                + "JOIN courses c ON a.course_id = c.course_id "
                + "JOIN course_students cs ON c.course_id = cs.course_id "
                + "WHERE cs.student_id = ? "
                + "ORDER BY a.due_date ASC";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            List<Assignment> assignments = new ArrayList<>();
            while (rs.next()) {
                Assignment assignment = new Assignment();
                assignment.setId(rs.getInt("id"));
                assignment.setTitle(rs.getString("title"));
                assignment.setDescription(rs.getString("description"));
                assignment.setDueDate(rs.getTimestamp("due_date"));
                assignment.setCourseName(rs.getString("course_name"));
                assignment.setCreatedAt(rs.getTimestamp("created_at"));
                assignments.add(assignment);
            }
            return assignments;
        }
    }
}