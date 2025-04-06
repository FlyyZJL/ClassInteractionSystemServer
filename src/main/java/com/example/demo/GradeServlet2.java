package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "GradeServlet2", urlPatterns = {"/api/grades/*", "/api/teacher/courses", "/api/course/*", "/api/grades/course/*", "/api/grades/export", "/api/course/*/statistics"})
public class GradeServlet2 extends HttpServlet {

    private GradeDAO2 gradeDAO;
    private CourseDao courseDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gradeDAO = new GradeDAO2();
        courseDAO = new CourseDao();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        // 添加调试日志,乱码如何处理
        System.out.println("请求URI: " + requestURI);
        System.out.println("Servlet路径: " + servletPath);
        System.out.println("路径信息: " + pathInfo);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (servletPath.equals("/api/course") && pathInfo != null && pathInfo.matches("/\\d+/statistics")) {
                // 获取课程统计信息
                getCourseStatistics(request, response);
                return;
            }

            if ("/api/teacher/courses".equals(servletPath)) {
                // 获取教师课程列表
                getTeacherCourses(request, response);
            } else if (servletPath.equals("/api/grades/course") && pathInfo != null) {
                // 获取课程成绩列表
                getCourseGrades(request, response);
            } else if (servletPath.equals("/api/course")) {
                // 处理 /api/course/* 路径
                if (pathInfo != null && pathInfo.matches("/\\d+/students")) {
                    // 匹配 /api/course/{courseId}/students 格式
                    getCourseStudents(request, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ApiResponse<>(false, "未找到请求的资源", null)));
                }
            } else if ("/api/grades/export".equals(servletPath)) {
                // 导出成绩
                exportGrades(request, response);
            } else if (servletPath.equals("/api/grades") && pathInfo != null && !pathInfo.equals("/")) {
                // 获取单个成绩详情
                getGradeDetail(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse<>(false, "未找到请求的资源", null)));
            }
        } catch (Exception e) {
            // 错误处理
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // 避免在已经使用getOutputStream的情况下调用getWriter
            if (!response.isCommitted()) {
                try {
                    out = response.getWriter();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print(gson.toJson(new ApiResponse<>(false, "服务器错误: " + e.getMessage(), null)));
                } catch (IllegalStateException ise) {
                    // 如果已经使用了OutputStream，则记录错误但不再尝试写入
                    System.err.println("无法写入错误响应：" + e.getMessage());
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if ("/api/grades".equals(servletPath) || "/api/grades/".equals(servletPath)) {
                // 添加成绩
                addGrade(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse<>(false, "未找到请求的资源", null)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse<>(false, "服务器错误: " + e.getMessage(), null)));
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (servletPath.equals("/api/grades") && pathInfo != null && !pathInfo.equals("/")) {
                // 更新成绩
                updateGrade(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse<>(false, "未找到请求的资源", null)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse<>(false, "服务器错误: " + e.getMessage(), null)));
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (servletPath.equals("/api/grades") && pathInfo != null && !pathInfo.equals("/")) {
                // 删除成绩
                deleteGrade(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse<>(false, "未找到请求的资源", null)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse<>(false, "服务器错误: " + e.getMessage(), null)));
            e.printStackTrace();
        }
    }

    /**
     * 获取教师的课程列表
     */
    private void getTeacherCourses(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String teacherIdParam = request.getParameter("teacherId");
        if (teacherIdParam == null || teacherIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "缺少必要参数: teacherId", null)));
            return;
        }

        int teacherId = Integer.parseInt(teacherIdParam);
        List<Course> courses = courseDAO.getTeacherCourses(teacherId);

        response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成功获取课程列表", courses)));
    }

    /**
     * 获取课程统计信息
     */
    private void getCourseStatistics(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String pathInfo = request.getPathInfo(); // 形如 "/6/statistics"
        System.out.println("获取课程统计信息，路径: " + pathInfo);

        // 从路径中提取课程ID
        int courseId;
        try {
            // 提取数字部分
            String courseIdStr = pathInfo.split("/")[1];
            courseId = Integer.parseInt(courseIdStr);
            System.out.println("解析的课程ID: " + courseId);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "无效的课程ID", null)));
            return;
        }

        try {
            // 获取统计信息
            GradeStatDAO statDAO = new GradeStatDAO();
            GradeStatistics statistics = statDAO.getCourseStatistics(courseId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成功获取课程统计信息", statistics)));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(gson.toJson(
                    new ApiResponse<>(false, "获取课程统计信息失败: " + e.getMessage(), null)
            ));
        }
    }

    /**
     * 获取课程的成绩列表
     */
    private void getCourseGrades(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "缺少课程ID", null)));
            return;
        }

        String teacherIdParam = request.getParameter("teacherId");
        if (teacherIdParam == null || teacherIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "缺少必要参数: teacherId", null)));
            return;
        }

        int teacherId = Integer.parseInt(teacherIdParam);
        int courseId = Integer.parseInt(pathInfo.substring(1)); // 移除前导斜杠

        List<Grade> grades = gradeDAO.getCourseGrades(courseId, teacherId);

        response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成功获取成绩列表", grades)));
    }

    /**
     * 获取课程的学生列表
     */
    private void getCourseStudents(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String pathInfo = request.getPathInfo(); // 形如 "/6/students"
        System.out.println("获取学生列表，路径: " + pathInfo);

        // 从路径中提取课程ID
        int courseId;
        try {
            // 提取数字部分
            String courseIdStr = pathInfo.split("/")[1];
            courseId = Integer.parseInt(courseIdStr);
            System.out.println("解析的课程ID: " + courseId);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "无效的课程ID", null)));
            return;
        }

        try {
            List<User> students = courseDAO.getCourseStudents(courseId);
            System.out.println("找到学生数量: " + students.size());
            response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成功获取学生列表", students)));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(gson.toJson(
                    new ApiResponse<>(false, "获取学生列表失败: " + e.getMessage(), null)
            ));
        }
    }
    /**
     * 获取成绩详情
     */
    private void getGradeDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String pathInfo = request.getPathInfo();
        int gradeId = Integer.parseInt(pathInfo.substring(1)); // 移除前导斜杠

        String teacherIdParam = request.getParameter("teacherId");
        if (teacherIdParam == null || teacherIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "缺少必要参数: teacherId", null)));
            return;
        }

        int teacherId = Integer.parseInt(teacherIdParam);
        Grade grade = gradeDAO.getGradeById(gradeId);

        if (grade == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "未找到指定成绩", null)));
            return;
        }

        // 检查教师是否有权限查看该成绩
        List<Course> teacherCourses = courseDAO.getTeacherCourses(teacherId);
        boolean hasPermission = teacherCourses.stream()
                .anyMatch(course -> course.getCourseId() == grade.getCourseId());

        if (!hasPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "无权访问此成绩", null)));
            return;
        }

        response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成功获取成绩详情", grade)));
    }

    /**
     * 添加成绩
     */
    private void addGrade(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        Grade grade = gson.fromJson(request.getReader(), Grade.class);

        // 数据验证
        if (grade.getCourseId() <= 0 || grade.getStudentId() <= 0 || grade.getGradeType() == null
                || grade.getGradeType().isEmpty() || grade.getScore() < 0 || grade.getScore() > 100) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "参数无效", null)));
            return;
        }

        // 检查教师是否有权限添加该课程的成绩
        List<Course> teacherCourses = courseDAO.getTeacherCourses(grade.getGradedBy());
        boolean hasPermission = teacherCourses.stream()
                .anyMatch(course -> course.getCourseId() == grade.getCourseId());

        if (!hasPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "无权为此课程添加成绩", null)));
            return;
        }

        Grade newGrade = gradeDAO.addGrade(grade);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成绩添加成功", newGrade)));
    }

    /**
     * 更新成绩
     */
    private void updateGrade(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String pathInfo = request.getPathInfo();
        int gradeId = Integer.parseInt(pathInfo.substring(1)); // 移除前导斜杠

        Grade grade = gson.fromJson(request.getReader(), Grade.class);
        grade.setGradeId(gradeId); // 确保ID正确

        // 数据验证
        if (grade.getCourseId() <= 0 || grade.getStudentId() <= 0 || grade.getGradeType() == null
                || grade.getGradeType().isEmpty() || grade.getScore() < 0 || grade.getScore() > 100) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "参数无效", null)));
            return;
        }

        // 检查成绩是否存在
        Grade existingGrade = gradeDAO.getGradeById(gradeId);
        if (existingGrade == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "未找到指定成绩", null)));
            return;
        }

        // 检查教师是否有权限更新该成绩
        List<Course> teacherCourses = courseDAO.getTeacherCourses(grade.getGradedBy());
        boolean hasPermission = teacherCourses.stream()
                .anyMatch(course -> course.getCourseId() == grade.getCourseId());

        if (!hasPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "无权更新此成绩", null)));
            return;
        }

        Grade updatedGrade = gradeDAO.updateGrade(grade);

        response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成绩更新成功", updatedGrade)));
    }

    /**
     * 删除成绩
     */
    private void deleteGrade(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String pathInfo = request.getPathInfo();
        int gradeId = Integer.parseInt(pathInfo.substring(1)); // 移除前导斜杠

        String teacherIdParam = request.getParameter("teacherId");
        if (teacherIdParam == null || teacherIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "缺少必要参数: teacherId", null)));
            return;
        }

        int teacherId = Integer.parseInt(teacherIdParam);

        boolean deleted = gradeDAO.deleteGrade(gradeId, teacherId);

        if (deleted) {
            response.getWriter().print(gson.toJson(new ApiResponse<>(true, "成绩删除成功", null)));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "未找到指定成绩或无权删除", null)));
        }
    }

    /**
     * 导出成绩
     */
    private void exportGrades(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ClassNotFoundException, IOException {

        String courseIdParam = request.getParameter("courseId");
        String teacherIdParam = request.getParameter("teacherId");
        String format = request.getParameter("format");

        if (teacherIdParam == null || teacherIdParam.isEmpty()) {
            // 错误处理时只使用getWriter()
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "缺少必要参数: teacherId", null)));
            return;
        }

        int teacherId = Integer.parseInt(teacherIdParam);
        int courseId = courseIdParam != null && !courseIdParam.isEmpty() && !courseIdParam.equals("all")
                ? Integer.parseInt(courseIdParam) : 0;

        // 导出CSV格式
        if ("csv".equals(format)) {
            // 不要在此处使用getWriter()，让exportGradesCSV处理输出
            System.out.println("导出成绩为CSV格式，课程ID: " + courseId + ", 教师ID: " + teacherId);

            exportGradesCSV(request, response, courseId, teacherId);

        } else {
            // 错误处理时只使用getWriter()
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(gson.toJson(new ApiResponse<>(false, "不支持的导出格式", null)));
        }
    }

    /**
     * 导出成绩为CSV格式
     */
    private void exportGradesCSV(HttpServletRequest request, HttpServletResponse response, int courseId, int teacherId)
            throws SQLException, ClassNotFoundException, IOException {

        // 清除任何可能的缓冲内容
        response.reset();

        // 设置响应头信息
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");

        // 设置文件名 - 确保正确编码中文文件名
        String fileName = courseId > 0 ? "grades_course_" + courseId + ".csv" : "all_grades.csv";
        // URL编码文件名，确保特殊字符正确传输
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        // 添加额外的响应头，禁止缓存
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // 获取成绩数据
        List<Grade> grades = gradeDAO.getGradesForExport(courseId, teacherId);

        // 创建内存缓冲区来构建完整的CSV内容
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 添加BOM标记，确保Excel正确识别UTF-8编码
        baos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });

        // 日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 写入CSV头
        String header = "\"课程\",\"学生\",\"成绩类型\",\"分数\",\"评语\",\"评分日期\",\"评分人\"\r\n";
        baos.write(header.getBytes(StandardCharsets.UTF_8));

        // 写入数据
        for (Grade grade : grades) {
            StringBuilder sb = new StringBuilder();
            // 在CSV中包含双引号的字段需要用双引号转义
            sb.append("\"").append(escapeCSV(grade.getCourseName())).append("\",");
            sb.append("\"").append(escapeCSV(grade.getStudentName())).append("\",");
            sb.append("\"").append(escapeCSV(grade.getGradeType())).append("\",");
            sb.append(grade.getScore()).append(",");

            String feedback = grade.getFeedback();
            if (feedback != null && !feedback.isEmpty()) {
                sb.append("\"").append(escapeCSV(feedback)).append("\",");
            } else {
                sb.append("\"\",");
            }

            sb.append("\"").append(dateFormat.format(grade.getGradeDate())).append("\",");

            String gradedByName = grade.getGradedByName();
            if (gradedByName != null) {
                sb.append("\"").append(escapeCSV(gradedByName)).append("\"");
            } else {
                sb.append("\"\"");
            }

            sb.append("\r\n");
            baos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        }

        // 获取完整的字节数组并设置内容长度
        byte[] csvBytes = baos.toByteArray();
        response.setContentLength(csvBytes.length);
        System.out.println("生成的CSV大小: " + csvBytes.length + " 字节");
        // 获取输出流并写入数据
        try (OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            out.write(csvBytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            // 如果已经开始发送响应，就不能再更改响应状态了
            if (!response.isCommitted()) {
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(gson.toJson(new ApiResponse<>(false, "导出失败: " + e.getMessage(), null)));
            }
        }
    }

    /**
     * 转义CSV字段中的特殊字符
     */
    private String escapeCSV(String field) {
        if (field == null) {
            return "";
        }
        // CSV格式中，双引号需要用两个双引号转义
        return field.replace("\"", "\"\"");
    }
}