package com.example.demo;

public class Course {

    private int courseId;  // 课程ID
    private String courseName;  // 课程名称
    private String description;  // 课程描述
    private int teacherId;  // 教师ID
    private String teacherName;  // 教师名称

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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    // toString 方法，方便打印
    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", description='" + description + '\'' +
                ", teacherId=" + teacherId +
                ", teacherName='" + teacherName + '\'' +
                '}';
    }
}

