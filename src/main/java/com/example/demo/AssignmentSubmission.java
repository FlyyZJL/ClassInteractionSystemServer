package com.example.demo;


import java.sql.Timestamp;
import java.util.Date;

public class AssignmentSubmission {
    private int submissionId;
    private int assignmentId;
    private int studentId;
    private String content;
    private String filePath;
    private Date submitTime;
    private Double score;
    private String feedback;
    private Integer gradedBy;
    private Date gradedAt;

    // 扩展字段
    private String studentName;
    private String assignmentTitle;
    private String gradedByName;

    // 默认构造函数
    public AssignmentSubmission() {
    }

    // 带参数的构造函数
    public AssignmentSubmission(int submissionId, int assignmentId, int studentId,
                                String content, String filePath, Date submitTime,
                                Double score, String feedback, Integer gradedBy, Date gradedAt) {
        this.submissionId = submissionId;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.content = content;
        this.filePath = filePath;
        this.submitTime = submitTime;
        this.score = score;
        this.feedback = feedback;
        this.gradedBy = gradedBy;
        this.gradedAt = gradedAt;
    }

    // Getters and Setters
    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(Integer gradedBy) {
        this.gradedBy = gradedBy;
    }

    public Date getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(Date gradedAt) {
        this.gradedAt = gradedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public String getGradedByName() {
        return gradedByName;
    }

    public void setGradedByName(String gradedByName) {
        this.gradedByName = gradedByName;
    }

    @Override
    public String toString() {
        return "AssignmentSubmission [submissionId=" + submissionId + ", assignmentId=" + assignmentId
                + ", studentId=" + studentId + ", submitTime=" + submitTime + ", score=" + score + "]";
    }
}