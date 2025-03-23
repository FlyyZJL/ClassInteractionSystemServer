package com.example.demo;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/teacher/submission-stats")
public class SubmissionStatsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        JsonObject result = new JsonObject();
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
            // 统计总数
            String totalSql = "SELECT COUNT(*) FROM course_students "
                    + "WHERE course_id = (SELECT course_id FROM assignments WHERE assignment_id = ?)";

            // 已提交数
            String submittedSql = "SELECT COUNT(DISTINCT student_id) FROM assignment_submissions "
                    + "WHERE assignment_id = ?";

            try (PreparedStatement totalPs = conn.prepareStatement(totalSql);
                 PreparedStatement submittedPs = conn.prepareStatement(submittedSql)) {

                totalPs.setInt(1, assignmentId);
                submittedPs.setInt(1, assignmentId);

                ResultSet totalRs = totalPs.executeQuery();
                ResultSet submittedRs = submittedPs.executeQuery();

                int total = totalRs.next() ? totalRs.getInt(1) : 0;
                int submitted = submittedRs.next() ? submittedRs.getInt(1) : 0;
                int unsubmitted = total - submitted;

                JsonObject stats = new JsonObject();
                stats.addProperty("total", total);
                stats.addProperty("submitted", submitted);
                stats.addProperty("unsubmitted", unsubmitted);

                result.add("data", stats);
                result.addProperty("success", true);
            }
        } catch (SQLException e) {
            result.addProperty("success", false);
            result.addProperty("message", "统计失败");
        }

        response.getWriter().print(result.toString());
    }
}