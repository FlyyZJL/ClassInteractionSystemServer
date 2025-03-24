package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
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

@WebServlet("/api/replies/post")
public class PostReplyServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PostReplyServlet.class);
    private static final Gson gson = new Gson();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Connection conn = null;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        try {
            // 读取并解析JSON
            JsonObject requestJson = readJsonRequest(req);

            // 提取参数
            int discussionId = getRequiredInt(requestJson, "discussion_id");
            int userId = getRequiredInt(requestJson, "user_id");
            String userRole = getRequiredString(requestJson, "user_role");
            String content = getRequiredString(requestJson, "content");
            Integer parentReplyId = getNullableInt(requestJson, "parent_reply_id");

            // 权限验证
            if (!validateDiscussionExistence(discussionId)) {
                sendError(resp, 404, "讨论帖不存在");
                return;
            }

            // 数据库操作
            conn = DatabaseUtils.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO discussion_replies (discussion_id, user_id, parent_reply_id, content) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, discussionId);
                ps.setInt(2, userId);
                if (parentReplyId != null) {
                    ps.setInt(3, parentReplyId);
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setString(4, content);

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("创建回复失败，未影响任何行");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    JsonObject responseJson = new JsonObject();
                    if (rs.next()) {
                        responseJson.addProperty("reply_id", rs.getInt(1));
                        resp.setStatus(HttpServletResponse.SC_CREATED);
                    } else {
                        throw new SQLException("无法获取回复ID");
                    }
                    resp.getWriter().print(responseJson.toString());
                }
                conn.commit();
            }
        } catch (JsonParseException | NullPointerException e) {
            sendError(resp, 400, "请求参数错误: " + e.getMessage());
        } catch (SQLException e) {
            rollbackConnection(conn);
            sendError(resp, 500, "数据库错误: " + e.getMessage());
            logger.error("数据库错误: ", e);
        } finally {
            DatabaseUtils.closeResources(conn, null, null);
        }
    }

    private JsonObject readJsonRequest(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new JsonParseException("无效的JSON格式");
        }
    }

    private int getRequiredInt(JsonObject json, String field) {
        if (!json.has(field)) {
            throw new NullPointerException("缺少必要字段: " + field);
        }
        try {
            return json.get(field).getAsInt();
        } catch (ClassCastException e) {
            throw new JsonParseException("字段类型错误: " + field);
        }
    }

    private String getRequiredString(JsonObject json, String field) {
        if (!json.has(field)) {
            throw new NullPointerException("缺少必要字段: " + field);
        }
        return json.get(field).getAsString();
    }

    private Integer getNullableInt(JsonObject json, String field) {
        if (!json.has(field) || json.get(field).isJsonNull()) {
            return null;
        }
        return json.get(field).getAsInt();
    }


    private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        JsonObject error = new JsonObject();
        error.addProperty("error", message);
        resp.getWriter().print(error.toString());
    }

    private void rollbackConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            logger.error("回滚事务失败", e);
        }
    }

    private boolean validateDiscussionExistence(int discussionId) throws SQLException {
        String sql = "SELECT 1 FROM discussions WHERE discussion_id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, discussionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}