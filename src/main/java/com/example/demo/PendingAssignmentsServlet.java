package com.example.demo;

import com.google.gson.JsonArray;
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

@WebServlet("/api/teacher/assignments/pending")
public class PendingAssignmentsServlet extends HttpServlet {

    /**
     * 处理GET请求：获取待批改作业列表
     * 流程：
     * 1. 验证教师权限
     * 2. 查询未评分的作业提交
     * 3. 返回JSON格式数据
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        /* SQL说明：
         * - 联表查询作业提交、用户、作业表
         * - s.file_path IS NOT NULL 转换为 has_attachment 布尔字段
         * - 只查询未评分（score IS NULL）的记录
         */
        String sql = "SELECT s.submission_id, u.username, a.title, s.submit_time, " +
                "s.file_path IS NOT NULL AS has_attachment " +
                "FROM assignment_submissions s " +
                "JOIN users u ON s.student_id = u.user_id " +
                "JOIN assignments a ON s.assignment_id = a.assignment_id " +
                "WHERE s.score IS NULL";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            JsonArray jsonArray = new JsonArray();

            // 遍历结果集构造JSON
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("submissionId", rs.getInt("submission_id"));
                obj.addProperty("studentName", rs.getString("username")); // 学生姓名
                obj.addProperty("assignmentTitle", rs.getString("title")); // 作业标题
                obj.addProperty("submitTime", rs.getTimestamp("submit_time").getTime()); // 提交时间戳
                obj.addProperty("hasAttachment", rs.getBoolean("has_attachment")); // 是否有附件
                jsonArray.add(obj);
            }

            // 设置响应类型并输出
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(jsonArray.toString());
        } catch (SQLException e) {
            response.sendError(500, "数据库查询失败");
        }
    }
}