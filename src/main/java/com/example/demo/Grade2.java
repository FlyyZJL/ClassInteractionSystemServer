package com.example.demo;


import java.io.Serializable;
import java.util.Date;

public class Grade2 implements Serializable {
    private int gradeId;
    private int courseId;
    private int studentId;
    private String gradeType;
    private double score;
    private String feedback;
    private Date gradeDate;
    private int gradedBy;

    // 扩展字段
    private String studentName;
    private String courseName;
    private String gradedByName;

    // 构造函数
    public Grade2() {
    }

    public Grade2(int courseId, int studentId, String gradeType, double score, String feedback, int gradedBy) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.gradeType = gradeType;
        this.score = score;
        this.feedback = feedback;
        this.gradedBy = gradedBy;
        this.gradeDate = new Date();
    }

    // Getters & Setters
    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getGradeType() { return gradeType; }
    public void setGradeType(String gradeType) { this.gradeType = gradeType; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Date getGradeDate() { return gradeDate; }
    public void setGradeDate(Date gradeDate) { this.gradeDate = gradeDate; }

    public int getGradedBy() { return gradedBy; }
    public void setGradedBy(int gradedBy) { this.gradedBy = gradedBy; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getGradedByName() { return gradedByName; }
    public void setGradedByName(String gradedByName) { this.gradedByName = gradedByName; }
}