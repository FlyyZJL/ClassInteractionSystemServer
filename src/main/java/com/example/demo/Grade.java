package com.example.demo;

import java.util.Date;

public class Grade {
    private int gradeId;
    private int courseId;
    private int studentId;
    private String gradeType;
    private double score;
    private String feedback;
    private Date gradeDate;
    private int gradedBy;

    // 扩展字段（不存储在数据库中）
    private String studentName;
    private String courseName;
    private String gradedByName;
    private String scoreClass; // high, medium, low

    // 默认构造函数
    public Grade() {
    }

    // 带参数的构造函数
    public Grade(int gradeId, int courseId, int studentId, String gradeType,
                 double score, String feedback, Date gradeDate, int gradedBy) {
        this.gradeId = gradeId;
        this.courseId = courseId;
        this.studentId = studentId;
        this.gradeType = gradeType;
        this.score = score;
        this.feedback = feedback;
        this.gradeDate = gradeDate;
        this.gradedBy = gradedBy;

        // 设置成绩等级
        calculateScoreClass();
    }

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
        calculateScoreClass();
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Date getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(Date gradeDate) {
        this.gradeDate = gradeDate;
    }

    public int getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(int gradedBy) {
        this.gradedBy = gradedBy;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getGradedByName() {
        return gradedByName;
    }

    public void setGradedByName(String gradedByName) {
        this.gradedByName = gradedByName;
    }

    public String getScoreClass() {
        return scoreClass;
    }

    public void setScoreClass(String scoreClass) {
        this.scoreClass = scoreClass;
    }

    // 计算成绩等级
    private void calculateScoreClass() {
        if (score >= 90) {
            this.scoreClass = "high";
        } else if (score >= 60) {
            this.scoreClass = "medium";
        } else {
            this.scoreClass = "low";
        }
    }

    @Override
    public String toString() {
        return "Grade [gradeId=" + gradeId + ", courseId=" + courseId + ", studentId=" + studentId
                + ", gradeType=" + gradeType + ", score=" + score + ", gradeDate=" + gradeDate + "]";
    }
}