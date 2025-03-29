package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

@WebServlet("/api/discussions/post")
public class PostDiscussionServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PostDiscussionServlet.class);
    private static final Gson gson = new Gson();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Connection conn = null;
        JsonObject responseJson = new JsonObject();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // 1. 读取请求体
            StringBuilder jsonPayload = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonPayload.append(line);
                }
            }
        // 在 PostDiscussionServlet 的 doPost 方法中增加
            System.out.println("Raw request body: " + jsonPayload.toString());
            // 2. 解析JSON数据
            JsonObject requestJson = JsonParser.parseString(jsonPayload.toString()).getAsJsonObject();

            // 3. 提取并验证参数
            int courseId = getRequiredInt(requestJson, "course_id");
            int userId = getRequiredInt(requestJson, "user_id");
            String userRole = getRequiredString(requestJson, "user_role");
            String title = getRequiredString(requestJson, "title");
            String content = getRequiredString(requestJson, "content");

            System.out.println("Received parameters:"+title+" "+content+" "+courseId+" "+userId+" "+userRole);

            // 4. 参数校验
            validateTitle(title);

            // 5. 权限验证
            if (!validateCourseMembership(userId, courseId, userRole)) {
                sendError(resp, 403, "用户无课程操作权限");
                return;
            }

            // 6. 执行数据库操作
            conn = DatabaseUtils.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO discussions (course_id, user_id, title, content) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setInt(1, courseId);
                ps.setInt(2, userId);
                ps.setString(3, title);
                ps.setString(4, content);

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("创建讨论失败，无行受影响");
                }

                // 7. 获取生成的主键
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        responseJson.addProperty("discussion_id", rs.getInt(1));
                        responseJson.addProperty("status", "success");
                        resp.setStatus(HttpServletResponse.SC_CREATED);
                    } else {
                        throw new SQLException("无法获取讨论ID");
                    }
                }
                conn.commit();
            }

            resp.getWriter().print(responseJson.toString());

        } catch (JsonParseException | NullPointerException e) {
            sendError(resp, 400, "请求参数错误: " + e.getMessage());
            logger.warn("参数解析错误: {}"+e.getMessage());
        } catch (IllegalArgumentException e) {
            sendError(resp, 400, e.getMessage());
            logger.warn("参数校验失败: {}"+e.getMessage());
        } catch (SQLException e) {
            rollbackConnection(conn);
            sendError(resp, 500, "数据库操作失败: " + e.getMessage());
            logger.error("数据库错误: ", e);
        } catch (Exception e) {
            sendError(resp, 500, "服务器内部错误");
            logger.error("未预期的错误: ", e);
        } finally {
            DatabaseUtils.closeResources(conn, null, null);
        }
    }

    // 参数验证辅助方法
    private int getRequiredInt(JsonObject json, String key) {
        if (!json.has(key)) {
            throw new IllegalArgumentException("缺少必要参数: " + key);
        }
        return json.get(key).getAsInt();
    }

    private String getRequiredString(JsonObject json, String key) {
        if (!json.has(key)) {
            throw new IllegalArgumentException("缺少必要参数: " + key);
        }
        return json.get(key).getAsString();
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (title.length() > 50) {
            throw new IllegalArgumentException("标题长度超过50字符限制");
        }
    }


    // 权限验证
    private boolean validateCourseMembership(int userId, int courseId, String role) {
        final String sql = "teacher".equals(role) ?
                "SELECT 1 FROM courses WHERE course_id = ? AND teacher_id = ?" :
                "SELECT 1 FROM course_students WHERE course_id = ? AND student_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("权限验证失败: courseId={}, userId={}, role={}",
                    courseId, userId, role, e);
            return false;
        }
    }

    // 错误处理
    private void sendError(HttpServletResponse resp, int code, String message)
            throws IOException {
        resp.setStatus(code);
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("error", message);
        resp.getWriter().print(gson.toJson(errorJson));
    }

    // 事务回滚
    private void rollbackConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("事务回滚失败: ", ex);
            }
        }
    }
}