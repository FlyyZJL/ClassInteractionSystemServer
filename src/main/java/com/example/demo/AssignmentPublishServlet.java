package com.example.demo;

import com.example.demo.DatabaseUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/teacher/assignments/publish")
public class AssignmentPublishServlet extends HttpServlet {
    private final Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        //解决乱码问题
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 解析请求参数
            JsonObject reqBody = gson.fromJson(request.getReader(), JsonObject.class);
            int courseId = reqBody.get("courseId").getAsInt();
            String title = reqBody.get("title").getAsString();
            String description = reqBody.get("description").getAsString();
            String dueDate = reqBody.get("dueDate").getAsString();

            // 解析日期（修正）
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.parse(dueDate, formatter));

            // 插入数据库
            String sql = "INSERT INTO assignments (course_id, title, description, due_date) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseUtils.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, courseId);
                ps.setString(2, title);
                ps.setString(3, description);
                ps.setTimestamp(4, timestamp);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            result.addProperty("assignmentId", rs.getInt(1));
                        }
                    }
                    result.addProperty("success", true);
                }
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "发布失败：" + e.getMessage());

            // 先返回 JSON，再设置状态码
            out.print(result);
            out.flush();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;  // 避免继续执行
        }

        out.print(result);
        out.flush();
    }
}
