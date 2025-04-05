package com.example.demo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/CourseServlet")
public class CourseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CourseDao courseDAO;
    private TeacherDao teacherDAO;

    public void init() {
        courseDAO = new CourseDao();
        teacherDAO = new TeacherDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if (action == null) {
                action = "listCourses";
            }

            switch(action) {
                case "listCourses":
                    listCourses(request, response);
                    break;
                default:
                    listCourses(request, response);
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=" + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            switch(action) {
                case "addCourse":
                    addCourse(request, response);
                    break;
                case "updateCourse":
                    updateCourse(request, response);
                    break;
                case "deleteCourse":
                    deleteCourse(request, response);
                    break;
                default:
                    listCourses(request, response);
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=" + e.getMessage());
        }
    }

    private void listCourses(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, ServletException, IOException {
        List<Course> coursesList = courseDAO.getAllCoursesWithDetails();
        List<User> teachersList = teacherDAO.getAllTeachers2();

        request.setAttribute("coursesList", coursesList);
        request.setAttribute("teachersList", teachersList);
        request.getRequestDispatcher("admin_courses.jsp").forward(request, response);
    }

    private void addCourse(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        String courseName = request.getParameter("courseName");
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        String description = request.getParameter("description");

        Course course = new Course();
        course.setCourseName(courseName);
        course.setTeacherId(teacherId);
        course.setDescription(description);

        courseDAO.addCourse(course);
        response.sendRedirect("CourseServlet");
    }

    private void updateCourse(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String courseName = request.getParameter("courseName");
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        String description = request.getParameter("description");

        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseName(courseName);
        course.setTeacherId(teacherId);
        course.setDescription(description);

        courseDAO.updateCourse(course);
        response.sendRedirect("CourseServlet");
    }

    private void deleteCourse(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        courseDAO.deleteCourse(courseId);
        response.sendRedirect("CourseServlet");
    }
}