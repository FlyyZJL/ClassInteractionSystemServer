package com.example.demo;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/teacher/export-unsubmitted")
public class ExportUnsubmittedServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 权限验证
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));

        try (Connection conn = DatabaseUtils.getConnection()) {
            // 验证教师是否有权限访问该作业
            String authSql = "SELECT COUNT(*) FROM assignments a "
                    + "JOIN courses c ON a.course_id = c.course_id "
                    + "WHERE a.assignment_id = ? AND c.teacher_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(authSql)) {
                ps.setInt(1, assignmentId);
                ps.setInt(2, teacherId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next() || rs.getInt(1) == 0) {
                    response.sendError(403, "无访问权限");
                    return;
                }
            }

            // 查询未交学生名单
            String querySql = "SELECT u.user_id, u.username, u.email "
                    + "FROM course_students cs "
                    + "JOIN users u ON cs.student_id = u.user_id "
                    + "LEFT JOIN assignment_submissions s ON s.student_id = u.user_id AND s.assignment_id = ? "
                    + "WHERE cs.course_id = (SELECT course_id FROM assignments WHERE assignment_id = ?) "
                    + "AND s.submission_id IS NULL";

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"unsubmitted_" + assignmentId + ".csv\"");

            try (PreparedStatement ps = conn.prepareStatement(querySql)) {
                ps.setInt(1, assignmentId);
                ps.setInt(2, assignmentId);

                ResultSet rs = ps.executeQuery();
                OutputStream out = response.getOutputStream();

                // 写入CSV头
                out.write("学号,姓名,邮箱\n".getBytes(StandardCharsets.UTF_8));

                // 写入数据
                while (rs.next()) {
                    String line = String.format("%s,%s,%s\n",
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("email"));
                    out.write(line.getBytes(StandardCharsets.UTF_8));
                }
                out.flush();
            }
        } catch (SQLException e) {
            response.sendError(500, "数据库错误");
        }
    }
}