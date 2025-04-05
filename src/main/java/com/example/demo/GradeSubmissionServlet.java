package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/teacher/grade-submission")
public class GradeSubmissionServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 解析请求体
            JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);

            // 参数验证
            if (!data.has("teacherId") || !data.has("role")
                    || !data.has("submissionId") || !data.has("score")) {
                response.setStatus(400);
                result.addProperty("message", "缺少必要参数");
                out.print(result.toString());
                return;
            }

            String role = data.get("role").getAsString();
            if (!"teacher".equals(role)) {
                response.setStatus(403);
                result.addProperty("message", "无操作权限");
                out.print(result.toString());
                return;
            }

            // 数据准备
            int teacherId = data.get("teacherId").getAsInt();
            int submissionId = data.get("submissionId").getAsInt();
            BigDecimal score = data.get("score").getAsBigDecimal();
            String feedback = data.has("feedback") ? data.get("feedback").getAsString() : null;

            // 分数范围验证
            if (score.compareTo(BigDecimal.ZERO) < 0
                    || score.compareTo(new BigDecimal(100)) > 0) {
                response.setStatus(400);
                result.addProperty("message", "分数必须在0-100之间");
                out.print(result.toString());
                return;
            }

            Connection conn = null;
            try {
                // 获取数据库连接并关闭自动提交
                conn = DatabaseUtils.getConnection();
                conn.setAutoCommit(false);

                // 1. 更新作业提交记录
                String updateSubmissionSql = "UPDATE assignment_submissions "
                        + "SET score = ?, feedback = ?, graded_by = ?, graded_at = NOW() "
                        + "WHERE submission_id = ?";

                try (PreparedStatement ps = conn.prepareStatement(updateSubmissionSql)) {
                    ps.setBigDecimal(1, score);
                    ps.setString(2, feedback);
                    ps.setInt(3, teacherId);
                    ps.setInt(4, submissionId);

                    int affected = ps.executeUpdate();
                    if (affected == 0) {
                        conn.rollback();
                        response.setStatus(404);
                        result.addProperty("message", "未找到作业记录");
                        return;
                    }
                }

                // 2. 获取学生ID、课程ID和作业标题
                String getInfoSql = "SELECT s.student_id, a.course_id, a.title FROM assignment_submissions s "
                        + "JOIN assignments a ON s.assignment_id = a.assignment_id "
                        + "WHERE s.submission_id = ?";

                int studentId = 0;
                int courseId = 0;
                String assignmentTitle = "";

                try (PreparedStatement ps = conn.prepareStatement(getInfoSql)) {
                    ps.setInt(1, submissionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            studentId = rs.getInt("student_id");
                            courseId = rs.getInt("course_id");
                            assignmentTitle = rs.getString("title");
                        } else {
                            conn.rollback();
                            response.setStatus(404);
                            result.addProperty("message", "未找到相关课程信息");
                            return;
                        }
                    }
                }

                // 构建成绩类型字符串
                String gradeType = "平时作业 - " + assignmentTitle;

                // 3. 检查成绩表中是否已存在该记录
                String checkGradeSql = "SELECT grade_id FROM grades "
                        + "WHERE course_id = ? AND student_id = ? AND grade_type = ?";

                int existingGradeId = 0;

                try (PreparedStatement ps = conn.prepareStatement(checkGradeSql)) {
                    ps.setInt(1, courseId);
                    ps.setInt(2, studentId);
                    ps.setString(3, gradeType);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            existingGradeId = rs.getInt("grade_id");
                        }
                    }
                }

                // 4. 根据是否存在决定更新或插入
                if (existingGradeId > 0) {
                    // 更新现有成绩记录
                    String updateGradeSql = "UPDATE grades SET "
                            + "score = ?, feedback = ?, graded_by = ?, grade_date = NOW() "
                            + "WHERE grade_id = ?";

                    try (PreparedStatement ps = conn.prepareStatement(updateGradeSql)) {
                        ps.setBigDecimal(1, score);
                        ps.setString(2, feedback);
                        ps.setInt(3, teacherId);
                        ps.setInt(4, existingGradeId);
                        ps.executeUpdate();
                    }
                } else {
                    // 插入新成绩记录
                    String insertGradeSql = "INSERT INTO grades "
                            + "(course_id, student_id, grade_type, score, feedback, graded_by) "
                            + "VALUES (?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement ps = conn.prepareStatement(insertGradeSql)) {
                        ps.setInt(1, courseId);
                        ps.setInt(2, studentId);
                        ps.setString(3, gradeType); // 使用包含作业标题的成绩类型
                        ps.setBigDecimal(4, score);
                        ps.setString(5, feedback);
                        ps.setInt(6, teacherId);
                        ps.executeUpdate();
                    }
                }

                // 提交事务
                conn.commit();

                result.addProperty("success", true);
                result.addProperty("message", "批改成功，成绩已同步");

            } catch (SQLException e) {
                // 发生错误时回滚事务
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                response.setStatus(500);
                result.addProperty("message", "数据库操作失败: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // 恢复自动提交并关闭连接
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            response.setStatus(500);
            result.addProperty("message", "服务器错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.print(result.toString());
        }
    }
}