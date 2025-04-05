package com.example.demo;

import com.google.gson.Gson;


import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/GradeServlet")
public class GradeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GradeDAO gradeDAO;
    private CourseDao courseDAO;
    private Gson gson;

    public void init() {
        gradeDAO = new GradeDAO();
        courseDAO = new CourseDao();
        gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "listGrades";
        }

        try {
            switch (action) {
                case "listGrades":
                    listGrades(request, response);
                    break;
                case "getGrade":
                    getGrade(request, response);
                    break;
                case "getStudentsForCourse":
                    getStudentsForCourse(request, response);
                    break;
                case "exportGrades":
                    exportGrades(request, response);
                    break;
                default:
                    listGrades(request, response);
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
                case "addGrade":
                    addGrade(request, response);
                    break;
                case "updateGrade":
                    updateGrade(request, response);
                    break;
                case "deleteGrade":
                    deleteGrade(request, response);
                    break;
                default:
                    response.sendRedirect("GradeServlet");
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            handleError(response, e);
        }
    }

    private void listGrades(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, ServletException, IOException {
        List<Grade> gradesList = gradeDAO.getAllGrades();
        List<Course> coursesList = courseDAO.getAllCourses();

        request.setAttribute("gradesList", gradesList);
        request.setAttribute("coursesList", coursesList);
        request.getRequestDispatcher("admin_grades.jsp").forward(request, response);
    }

    private void getGrade(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int gradeId = Integer.parseInt(request.getParameter("gradeId"));
        Grade grade = gradeDAO.getGradeById(gradeId);

        if (grade != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(grade));
            out.flush();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"成绩记录不存在\"}");
        }
    }

    private void getStudentsForCourse(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        List<User> students = gradeDAO.getStudentsForCourse(courseId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(students));
        out.flush();
    }

    private void addGrade(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        String gradeType = request.getParameter("gradeType");
        double score = Double.parseDouble(request.getParameter("score"));
        String feedback = request.getParameter("feedback");

        // 获取当前管理员ID
        HttpSession session = request.getSession();
        int gradedBy = (Integer) session.getAttribute("user_id");

        // 检查是否已存在相同类型的成绩
        if (gradeDAO.gradeExists(courseId, studentId, gradeType)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"该学生的此类型成绩已存在！\"}");
            return;
        }

        Grade grade = new Grade();
        grade.setCourseId(courseId);
        grade.setStudentId(studentId);
        grade.setGradeType(gradeType);
        grade.setScore(score);
        grade.setFeedback(feedback);
        grade.setGradedBy(gradedBy);

        gradeDAO.addGrade(grade);
        response.sendRedirect("GradeServlet");
    }

    private void updateGrade(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int gradeId = Integer.parseInt(request.getParameter("gradeId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        String gradeType = request.getParameter("gradeType");
        double score = Double.parseDouble(request.getParameter("score"));
        String feedback = request.getParameter("feedback");

        // 获取当前管理员ID
        HttpSession session = request.getSession();
        int gradedBy = (Integer) session.getAttribute("user_id");

        // 获取原始成绩记录
        Grade originalGrade = gradeDAO.getGradeById(gradeId);

        // 检查是否已存在相同类型的成绩（除了当前记录）
        if ((originalGrade.getCourseId() != courseId ||
                originalGrade.getStudentId() != studentId ||
                !originalGrade.getGradeType().equals(gradeType)) &&
                gradeDAO.gradeExists(courseId, studentId, gradeType)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"该学生的此类型成绩已存在！\"}");
            return;
        }

        Grade grade = new Grade();
        grade.setGradeId(gradeId);
        grade.setCourseId(courseId);
        grade.setStudentId(studentId);
        grade.setGradeType(gradeType);
        grade.setScore(score);
        grade.setFeedback(feedback);
        grade.setGradedBy(gradedBy);

        gradeDAO.updateGrade(grade);
        response.sendRedirect("GradeServlet");
    }

    private void deleteGrade(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        int gradeId = Integer.parseInt(request.getParameter("gradeId"));
        gradeDAO.deleteGrade(gradeId);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void exportGrades(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {
        String format = request.getParameter("format");
        String courseIdParam = request.getParameter("courseId");
        Integer courseId = null;

        if (courseIdParam != null && !courseIdParam.equals("all")) {
            courseId = Integer.parseInt(courseIdParam);
        }

        List<Grade> grades = gradeDAO.getGradesForExport(courseId);

        if (format.equals("csv")) {
            exportGradesCSV(request, response, grades, courseId);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("不支持的导出格式");
        }
    }

    private void exportGradesCSV(HttpServletRequest request, HttpServletResponse response, List<Grade> grades, Integer courseId)
            throws IOException, SQLException, ClassNotFoundException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");

        // 设置文件名
        String fileName = courseId != null ? "grades_course_" + courseId + ".csv" : "all_grades.csv";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // 使用OutputStream统一处理所有输出
        ServletOutputStream out = response.getOutputStream();

        // 添加BOM标记，确保Excel正确识别UTF-8编码
        byte[] bomBytes = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
        out.write(bomBytes);

        // 日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 写入CSV头
        String header = "\"课程\",\"学生\",\"成绩类型\",\"分数\",\"评语\",\"评分日期\",\"评分人\"\r\n";
        out.write(header.getBytes("UTF-8"));

        // 写入数据
        for (Grade grade : grades) {
            StringBuilder sb = new StringBuilder();
            // 在CSV中包含双引号的字段需要用双引号转义
            sb.append("\"").append(grade.getCourseName().replace("\"", "\"\"")).append("\",");
            sb.append("\"").append(grade.getStudentName().replace("\"", "\"\"")).append("\",");
            sb.append("\"").append(grade.getGradeType().replace("\"", "\"\"")).append("\",");
            sb.append(grade.getScore()).append(",");

            String feedback = grade.getFeedback();
            if (feedback != null && !feedback.isEmpty()) {
                sb.append("\"").append(feedback.replace("\"", "\"\"")).append("\",");
            } else {
                sb.append("\"\",");
            }

            sb.append("\"").append(dateFormat.format(grade.getGradeDate())).append("\",");

            String gradedByName = grade.getGradedByName();
            if (gradedByName != null) {
                sb.append("\"").append(gradedByName.replace("\"", "\"\"")).append("\"");
            } else {
                sb.append("\"\"");
            }

            // 添加行结束符并写入
            sb.append("\r\n");
            out.write(sb.toString().getBytes("UTF-8"));
        }

        // 确保数据被发送
        out.flush();
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