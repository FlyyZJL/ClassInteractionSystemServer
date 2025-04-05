package com.example.demo;

import java.sql.Timestamp;
import java.util.Date;

public class Assignment {
    private int assignmentId;
    private int courseId;
    private String title;
    private String description;
    private Date dueDate;
    private Timestamp createdAt;

    // 扩展字段（不存储在数据库中）
    private String courseName;
    private int submissionCount;
    private int totalStudents;
    private boolean isOverdue;

    // 默认构造函数
    public Assignment() {
    }

    // 带参数的构造函数
    public Assignment(int assignmentId, int courseId, String title, String description, Date dueDate, Timestamp createdAt) {
        this.assignmentId = assignmentId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        // 自动设置是否已截止
        this.isOverdue = dueDate != null && dueDate.before(new Date());
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    @Override
    public String toString() {
        return "Assignment [assignmentId=" + assignmentId + ", courseId=" + courseId + ", title=" + title
                + ", dueDate=" + dueDate + ", isOverdue=" + isOverdue + "]";
    }
}