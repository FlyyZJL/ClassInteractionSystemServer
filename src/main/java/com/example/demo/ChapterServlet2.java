package com.example.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/ChapterServlet")
public class ChapterServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ChapterDAO chapterDAO;
    private Gson gson;

    public void init() {
        chapterDAO = new ChapterDAO();
        gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        try {
            String action = request.getParameter("action");
            if (action == null) {
                action = "getChapters";
            }

            switch(action) {
                case "getChapters":
                    getChapters(request, response);
                    break;
                default:
                    getChapters(request, response);
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("数据库错误：" + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            switch(action) {
                case "addChapter":
                    addChapter(request, response);
                    break;
                case "updateChapter":
                    updateChapter(request, response);
                    break;
                case "deleteChapter":
                    deleteChapter(request, response);
                    break;
                default:
                    response.sendRedirect("CourseServlet");
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=" + e.getMessage());
        }
    }

    private void getChapters(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        List<Chapter2> chapters = chapterDAO.getChaptersByCourseId(courseId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(chapters));
        out.flush();
    }

    private void addChapter(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String videoUrl = request.getParameter("videoUrl");

        Chapter2 chapter = new Chapter2();
        chapter.setCourseId(courseId);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setVideoUrl(videoUrl);

        chapterDAO.addChapter(chapter);
        response.sendRedirect("CourseServlet?action=listChapters&courseId=" + courseId);
    }

    private void updateChapter(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int chapterId = Integer.parseInt(request.getParameter("chapterId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String videoUrl = request.getParameter("videoUrl");

        Chapter2 chapter = new Chapter2();
        chapter.setId(chapterId);
        chapter.setCourseId(courseId);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setVideoUrl(videoUrl);

        chapterDAO.updateChapter(chapter);
        response.sendRedirect("CourseServlet?action=listChapters&courseId=" + courseId);
    }

    private void deleteChapter(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int chapterId = Integer.parseInt(request.getParameter("chapterId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        chapterDAO.deleteChapter(chapterId);
        response.sendRedirect("CourseServlet?action=listChapters&courseId=" + courseId);
    }
}