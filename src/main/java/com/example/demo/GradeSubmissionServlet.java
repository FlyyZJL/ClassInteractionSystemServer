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

            // 更新数据库
            String sql = "UPDATE assignment_submissions "
                    + "SET score = ?, feedback = ?, graded_by = ?, graded_at = NOW() "
                    + "WHERE submission_id = ?";

            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setBigDecimal(1, score);
                ps.setString(2, feedback);
                ps.setInt(3, teacherId);
                ps.setInt(4, submissionId);

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    result.addProperty("success", true);
                    result.addProperty("message", "批改成功");
                } else {
                    response.setStatus(404);
                    result.addProperty("message", "未找到作业记录");
                }

            } catch (SQLException e) {
                response.setStatus(500);
                result.addProperty("message", "数据库更新失败");
            }

        } catch (Exception e) {
            response.setStatus(500);
            result.addProperty("message", "服务器错误");
        } finally {
            out.print(result.toString());
        }
    }
}
