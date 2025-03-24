package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/chapters")
public class ChapterServlet extends HttpServlet {

    // 创建章节
    // ChapterServlet.java 完整修正
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            Chapter chapter = new Gson().fromJson(reader, Chapter.class);
            // 在ChapterServlet中添加日志
            System.out.println("Received chapter data: " + new Gson().toJson(chapter));
            // 添加空值检查
            if (chapter.getVideoUrl() == null) {
                System.out.println("警告：视频URL为空");
            }

            String sql = "INSERT INTO chapters (course_id, title, content, video_url) VALUES (?,?,?,?)";
            int result = DatabaseUtils.executeUpdate(sql, new Object[]{
                    chapter.getCourseId(),
                    chapter.getTitle(),
                    chapter.getContent(),
                    chapter.getVideoUrl()  // 确保这里获取到值
            });

            System.out.println("插入结果：" + result + " 行受影响");
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (SQLException e) {
            System.err.println("数据库错误：" + e.getMessage());
            resp.sendError(500, "Database error: " + e.getMessage());
        }
    }

    // 获取章节列表
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //处理乱码
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=utf-8");


        int courseId = Integer.parseInt(req.getParameter("courseId"));

        try {
            String sql = "SELECT * FROM chapters WHERE course_id = ?";
            ResultSet rs = DatabaseUtils.executeQuery(sql, new Object[]{courseId});

            List<Chapter> chapters = new ArrayList<>();
            while (rs.next()) {
                Chapter chapter = new Chapter();
                chapter.setId(rs.getInt("id"));
                chapter.setCourseId(rs.getInt("course_id"));
                chapter.setTitle(rs.getString("title"));
                chapter.setContent(rs.getString("content"));
                chapter.setVideoUrl(rs.getString("video_url"));
                chapters.add(chapter);
            }

            resp.getWriter().write(new Gson().toJson(chapters));
        } catch (SQLException e) {
            resp.sendError(500, "Database error");
        }
    }
}