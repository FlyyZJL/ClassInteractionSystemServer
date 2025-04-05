package com.example.demo;


import com.google.gson.Gson;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/AssignmentServlet")
public class AssignmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AssignmentDAO assignmentDAO;
    private CourseDao courseDAO;
    private Gson gson;

    public void init() {
        assignmentDAO = new AssignmentDAO();
        courseDAO = new CourseDao();
        gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "listAssignments";
        }

        try {
            switch (action) {
                case "listAssignments":
                    listAssignments(request, response);
                    break;
                case "getAssignment":
                    getAssignment(request, response);
                    break;
                default:
                    listAssignments(request, response);
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

        try {
            switch (action) {
                case "addAssignment":
                    addAssignment(request, response);
                    break;
                case "updateAssignment":
                    updateAssignment(request, response);
                    break;
                case "deleteAssignment":
                    deleteAssignment(request, response);
                    break;
                default:
                    response.sendRedirect("AssignmentServlet");
                    break;
            }
        } catch (SQLException | ClassNotFoundException | ParseException e) {
            handleError(response, e);
        }
    }

    private void listAssignments(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, ServletException, IOException {
        List<Assignment> assignmentsList = assignmentDAO.getAllAssignments();
        List<Course> coursesList = courseDAO.getAllCourses();

        request.setAttribute("assignmentsList", assignmentsList);
        request.setAttribute("coursesList", coursesList);
        request.getRequestDispatcher("admin_assignments.jsp").forward(request, response);
    }

    private void getAssignment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
        Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(assignment));
        out.flush();
    }

    private void addAssignment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException, ParseException {
        String title = request.getParameter("title");
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String description = request.getParameter("description");

        // 解析日期时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date dueDate = dateFormat.parse(request.getParameter("dueDate"));

        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setCourseId(courseId);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);

        assignmentDAO.addAssignment(assignment);
        response.sendRedirect("AssignmentServlet");
    }

    private void updateAssignment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException, ParseException {
        int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
        String title = request.getParameter("title");
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String description = request.getParameter("description");

        // 解析日期时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date dueDate = dateFormat.parse(request.getParameter("dueDate"));

        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setTitle(title);
        assignment.setCourseId(courseId);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);

        assignmentDAO.updateAssignment(assignment);
        response.sendRedirect("AssignmentServlet");
    }

    private void deleteAssignment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
        assignmentDAO.deleteAssignment(assignmentId);
        response.setStatus(HttpServletResponse.SC_OK);
    }

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