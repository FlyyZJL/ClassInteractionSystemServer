package com.example.demo;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/DiscussionServlet")
public class DiscussionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DiscussionDAO discussionDAO;
    private DiscussionReplyDAO replyDAO;
    private CourseDao courseDAO;
    private Gson gson;

    public void init() {
        discussionDAO = new DiscussionDAO();
        replyDAO = new DiscussionReplyDAO();
        courseDAO = new CourseDao();
        gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "listDiscussions";
        }

        try {
            switch (action) {
                case "listDiscussions":
                    listDiscussions(request, response);
                    break;
                case "getDiscussion":
                    getDiscussion(request, response);
                    break;
                case "getReplies":
                    getReplies(request, response);
                    break;
                default:
                    listDiscussions(request, response);
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            handleError(response, e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("admin_discussions.jsp");
            return;
        }

        try {
            switch (action) {
                case "addDiscussion":
                    addDiscussion(request, response);
                    break;
                case "updateDiscussion":
                    updateDiscussion(request, response);
                    break;
                case "deleteDiscussion":
                    deleteDiscussion(request, response);
                    break;
                case "pinDiscussion":
                    pinDiscussion(request, response);
                    break;
                case "unpinDiscussion":
                    unpinDiscussion(request, response);
                    break;
                case "addReply":
                    addReply(request, response);
                    break;
                case "updateReply":
                    updateReply(request, response);
                    break;
                case "deleteReply":
                    deleteReply(request, response);
                    break;
                default:
                    response.sendRedirect("admin_discussions.jsp");
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            handleError(response, e);
        }
    }

    // 列出所有讨论
    private void listDiscussions(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, ServletException, IOException {
        List<Discussion> discussionsList = discussionDAO.getAllDiscussions();
        List<Course> coursesList = courseDAO.getAllCourses();

        request.setAttribute("discussionsList", discussionsList);
        request.setAttribute("coursesList", coursesList);
        request.getRequestDispatcher("admin_discussions.jsp").forward(request, response);
    }

    // 获取单个讨论详情（JSON格式）
    private void getDiscussion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        Discussion discussion = discussionDAO.getDiscussionById(discussionId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(discussion));
        out.flush();
    }

    // 获取讨论的所有回复（JSON格式）
    private void getReplies(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        List<DiscussionReply> replies = replyDAO.getRepliesByDiscussionId(discussionId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(replies));
        out.flush();
    }

    // 添加新讨论
    private void addDiscussion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        String title = request.getParameter("title");
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String content = request.getParameter("content");
        boolean isPinned = request.getParameter("isPinned") != null;

        // 获取当前用户ID
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("user_id");

        Discussion discussion = new Discussion();
        discussion.setCourseId(courseId);
        discussion.setUserId(userId);
        discussion.setTitle(title);
        discussion.setContent(content);
        discussion.setIsPinned(isPinned);

        discussionDAO.addDiscussion(discussion);
        response.sendRedirect("DiscussionServlet?action=listDiscussions");
    }

    // 更新讨论
    private void updateDiscussion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        String title = request.getParameter("title");
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String content = request.getParameter("content");
        boolean isPinned = request.getParameter("isPinned") != null;

        Discussion discussion = new Discussion();
        discussion.setDiscussionId(discussionId);
        discussion.setCourseId(courseId);
        discussion.setTitle(title);
        discussion.setContent(content);
        discussion.setIsPinned(isPinned);

        discussionDAO.updateDiscussion(discussion);
        response.sendRedirect("DiscussionServlet?action=listDiscussions");
    }

    // 删除讨论
    private void deleteDiscussion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        discussionDAO.deleteDiscussion(discussionId);
        response.sendRedirect("DiscussionServlet?action=listDiscussions");
    }

    // 置顶讨论
    private void pinDiscussion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        discussionDAO.pinDiscussion(discussionId);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // 取消置顶讨论
    private void unpinDiscussion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        discussionDAO.unpinDiscussion(discussionId);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // 添加回复
    private void addReply(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        String content = request.getParameter("content");
        String parentReplyIdParam = request.getParameter("parentReplyId");
        Integer parentReplyId = null;

        if (parentReplyIdParam != null && !parentReplyIdParam.isEmpty()) {
            parentReplyId = Integer.parseInt(parentReplyIdParam);
        }

        // 获取当前用户ID
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("user_id");

        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussionId(discussionId);
        reply.setUserId(userId);
        reply.setParentReplyId(parentReplyId);
        reply.setContent(content);

        replyDAO.addReply(reply);

        // 可以根据请求来源，决定重定向到列表页或者详情页
        String referer = request.getParameter("referer");
        if ("details".equals(referer)) {
            response.sendRedirect("DiscussionServlet?action=getDiscussion&discussionId=" + discussionId);
        } else {
            response.sendRedirect("DiscussionServlet?action=listDiscussions");
        }
    }

    // 更新回复
    private void updateReply(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int replyId = Integer.parseInt(request.getParameter("replyId"));
        int discussionId = Integer.parseInt(request.getParameter("discussionId"));
        String content = request.getParameter("content");

        DiscussionReply reply = new DiscussionReply();
        reply.setReplyId(replyId);
        reply.setContent(content);

        replyDAO.updateReply(reply);

        // 根据请求来源决定重定向
        String referer = request.getParameter("referer");
        if ("details".equals(referer)) {
            response.sendRedirect("DiscussionServlet?action=getDiscussion&discussionId=" + discussionId);
        } else {
            response.sendRedirect("DiscussionServlet?action=listDiscussions");
        }
    }

    // 删除回复
    private void deleteReply(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int replyId = Integer.parseInt(request.getParameter("replyId"));
        replyDAO.deleteReply(replyId);

        // 如果请求中没有discussionId参数，可能是从AJAX调用，返回成功状态
        String discussionIdParam = request.getParameter("discussionId");
        if (discussionIdParam == null || discussionIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // 否则重定向到讨论详情页
            int discussionId = Integer.parseInt(discussionIdParam);
            response.sendRedirect("DiscussionServlet?action=getDiscussion&discussionId=" + discussionId);
        }
    }

    // 错误处理
    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        PrintWriter out = response.getWriter();
        out.print("{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        out.flush();

        e.printStackTrace();
    }
}