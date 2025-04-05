package com.example.demo;


import java.sql.Timestamp;
public class Chapter2 {
    private int id;
    private int courseId;
    private String title;
    private String content;
    private String videoUrl;
    private Timestamp createdAt;

    // 默认构造函数
    public Chapter2() {
    }

    // 带参数的构造函数
    public Chapter2(int id, int courseId, String title, String content, String videoUrl, Timestamp createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Chapter [id=" + id + ", courseId=" + courseId + ", title=" + title + ", content="
                + (content != null ? content.substring(0, Math.min(content.length(), 50)) + "..." : "null")
                + ", videoUrl=" + videoUrl + "]";
    }
}