package com.example.demo;

import java.sql.Timestamp;

public class Course {

    private int courseId;  // 课程ID
    private String courseName;  // 课程名称
    private String description;  // 课程描述
    private int teacherId;  // 教师ID
    private String teacherName;  // 教师名称

    private String teacherEmail;
    private Timestamp createdAt;

    // 扩展字段（不存储在数据库中）
    private int chapterCount;
    private int studentCount;


    // 构造方法
    public Course(int courseId, String courseName, String description, int teacherId, String teacherName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    public Course(String courseName, String courseDescription, int teacherId) {
        this.courseName = courseName;
        this.description = courseDescription;
        this.teacherId = teacherId;
    }


    // 带参数的构造函数
    public Course(int courseId, String courseName, int teacherId, String description, Timestamp createdAt) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.description = description;
        this.createdAt = createdAt;
    }

    // 默认构造函数
    public Course() {
    }



    // Getter 和 Setter 方法
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }
    // toString 方法，方便打印
    @Override
    public String toString() {
        return "Course [courseId=" + courseId + ", courseName=" + courseName + ", teacherId=" + teacherId
                + ", description=" + description + ", teacherName=" + teacherName + ", chapterCount=" + chapterCount
                + ", studentCount=" + studentCount + "]";
    }
}

