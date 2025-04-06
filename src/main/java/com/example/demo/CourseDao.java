package com.example.demo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    // 根据教师ID查询该教师所教授的所有课程
    public List<Course> getCoursesByTeacherId(int teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_name, c.description, u.username AS teacher_name " +
                "FROM courses c " +
                "JOIN users u ON c.teacher_id = u.user_id " +
                "WHERE c.teacher_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);  // 设置查询参数（教师ID）

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int courseId = rs.getInt("course_id");
                    String courseName = rs.getString("course_name");
                    String description = rs.getString("description");
                    String teacherName = rs.getString("teacher_name");

                    Course course = new Course(courseId, courseName, description, teacherId, teacherName);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }
    // 获取所有课程基本信息
    public List<Course> getAllCourses() throws SQLException, ClassNotFoundException {
        List<Course> courses = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM courses ORDER BY course_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setTeacherId(rs.getInt("teacher_id"));
                course.setDescription(rs.getString("description"));
                course.setCreatedAt(rs.getTimestamp("created_at"));
                courses.add(course);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return courses;
    }

    // 获取所有课程详细信息（包括教师姓名、章节数和学生数）
    public List<Course> getAllCoursesWithDetails() throws SQLException, ClassNotFoundException {
        List<Course> courses = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT c.course_id, c.course_name, c.teacher_id, c.description, c.created_at, " +
                    "u.username AS teacher_name, " +
                    "(SELECT COUNT(*) FROM chapters ch WHERE ch.course_id = c.course_id) AS chapter_count, " +
                    "(SELECT COUNT(*) FROM course_students cs WHERE cs.course_id = c.course_id) AS student_count " +
                    "FROM courses c " +
                    "JOIN users u ON c.teacher_id = u.user_id " +
                    "ORDER BY c.course_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setTeacherId(rs.getInt("teacher_id"));
                course.setDescription(rs.getString("description"));
                course.setCreatedAt(rs.getTimestamp("created_at"));
                course.setTeacherName(rs.getString("teacher_name"));
                course.setChapterCount(rs.getInt("chapter_count"));
                course.setStudentCount(rs.getInt("student_count"));
                courses.add(course);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return courses;
    }

    // 根据ID获取课程
    public Course getCourseById(int courseId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Course course = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT c.*, u.username AS teacher_name FROM courses c " +
                    "JOIN users u ON c.teacher_id = u.user_id " +
                    "WHERE c.course_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setTeacherId(rs.getInt("teacher_id"));
                course.setDescription(rs.getString("description"));
                course.setCreatedAt(rs.getTimestamp("created_at"));
                course.setTeacherName(rs.getString("teacher_name"));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        return course;
    }

    // 添加课程
    public void addCourse(Course course) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO courses (course_name, teacher_id, description) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, course.getCourseName());
            stmt.setInt(2, course.getTeacherId());
            stmt.setString(3, course.getDescription());
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // 更新课程
    public void updateCourse(Course course) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE courses SET course_name = ?, teacher_id = ?, description = ? WHERE course_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, course.getCourseName());
            stmt.setInt(2, course.getTeacherId());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getCourseId());
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // 删除课程（同时删除相关章节和其他关联数据）
    public void deleteCourse(int courseId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 先删除课程的章节
            String deleteChaptersSql = "DELETE FROM chapters WHERE course_id = ?";
            stmt1 = conn.prepareStatement(deleteChaptersSql);
            stmt1.setInt(1, courseId);
            stmt1.executeUpdate();

            // 删除课程的学生关联
            String deleteCourseStudentsSql = "DELETE FROM course_students WHERE course_id = ?";
            stmt2 = conn.prepareStatement(deleteCourseStudentsSql);
            stmt2.setInt(1, courseId);
            stmt2.executeUpdate();

            // 最后删除课程本身
            String deleteCourseSql = "DELETE FROM courses WHERE course_id = ?";
            stmt3 = conn.prepareStatement(deleteCourseSql);
            stmt3.setInt(1, courseId);
            stmt3.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw ex;
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
            if (stmt1 != null) stmt1.close();
            if (stmt2 != null) stmt2.close();
            if (stmt3 != null) stmt3.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * 获取教师的所有课程
     */
    public List<Course> getTeacherCourses(int teacherId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM courses WHERE teacher_id = ? ORDER BY course_name";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teacherId);

            rs = stmt.executeQuery();
            List<Course> courses = new ArrayList<>();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setTeacherId(rs.getInt("teacher_id"));
                course.setDescription(rs.getString("description"));
                course.setCreatedAt(rs.getTimestamp("created_at"));
                courses.add(course);
            }

            return courses;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    /**
     * 获取课程的学生列表
     */
    public List<User> getCourseStudents(int courseId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT u.* FROM users u " +
                    "JOIN course_students cs ON u.user_id = cs.student_id " +
                    "WHERE cs.course_id = ? AND u.user_type = 'student' " +
                    "ORDER BY u.username";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);

            rs = stmt.executeQuery();
            List<User> students = new ArrayList<>();

            while (rs.next()) {
                User student = new User();
                student.setUserId(rs.getInt("user_id"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                student.setUserType(rs.getString("user_type"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                students.add(student);
            }

            return students;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }
}
