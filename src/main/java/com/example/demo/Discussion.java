package com.example.demo;

import java.sql.Timestamp;

public class Discussion {
    private int discussionId;
    private int courseId;
    private int userId;
    private String title;
    private String content;
    private Timestamp createdAt;
    private boolean isPinned;
    private Timestamp pinnedAt;
    private boolean isDeleted;

    // 扩展字段（不存储在数据库中）
    private String username;
    private String courseName;
    private int replyCount;

    // 默认构造函数
    public Discussion() {
    }

    // 带参数的构造函数
    public Discussion(int discussionId, int courseId, int userId, String title, String content,
                      Timestamp createdAt, boolean isPinned, Timestamp pinnedAt, boolean isDeleted) {
        this.discussionId = discussionId;
        this.courseId = courseId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.isPinned = isPinned;
        this.pinnedAt = pinnedAt;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public int getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(int discussionId) {
        this.discussionId = discussionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    public Timestamp getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(Timestamp pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    @Override
    public String toString() {
        return "Discussion [discussionId=" + discussionId + ", courseId=" + courseId + ", userId=" + userId
                + ", title=" + title + ", isPinned=" + isPinned + ", isDeleted=" + isDeleted + "]";
    }
}