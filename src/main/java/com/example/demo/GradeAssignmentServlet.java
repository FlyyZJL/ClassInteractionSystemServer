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

@WebServlet("/api/teacher/assignments/grade")
public class GradeAssignmentServlet extends HttpServlet {
    /**
     * 处理POST请求：提交作业评分
     * 请求体格式：
     * {
     *   "teacherId": 2023001,    // 新增教师ID参数
     *   "role": "teacher",       // 新增角色参数
     *   "submissionId": 123,
     *   "score": 85.5,
     *   "feedback": "评语内容"
     * }
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 解析JSON请求体
            JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);

            // 参数校验（新增）
            if (!data.has("teacherId") || !data.has("role")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.addProperty("message", "缺少身份参数");
                out.print(result.toString());
                return;
            }

            // 验证教师身份（修改后）
            String role = data.get("role").getAsString();
            if (!"teacher".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                result.addProperty("message", "无教师权限");
                out.print(result.toString());
                return;
            }

            // 参数格式验证
            int teacherId;
            int submissionId;
            try {
                teacherId = data.get("teacherId").getAsInt();
                submissionId = data.get("submissionId").getAsInt();
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.addProperty("message", "参数格式错误");
                out.print(result.toString());
                return;
            }

            // SQL保持不变
            String sql = "UPDATE assignment_submissions SET " +
                    "score = ?, feedback = ?, graded_by = ?, graded_at = NOW() " +
                    "WHERE submission_id = ?";

            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                // 参数绑定（修改后使用请求参数）
                ps.setBigDecimal(1, new BigDecimal(data.get("score").getAsString()));
                ps.setString(2, data.get("feedback").getAsString());
                ps.setInt(3, teacherId);  // 使用请求中的teacherId
                ps.setInt(4, submissionId);

                int affected = ps.executeUpdate();
                result.addProperty("success", affected > 0);
                result.addProperty("message", affected > 0 ? "批改成功" : "提交ID不存在");

            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.addProperty("message", "数据库操作失败");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.addProperty("message", "非法请求格式");
        } finally {
            out.print(result.toString());
        }
    }
}