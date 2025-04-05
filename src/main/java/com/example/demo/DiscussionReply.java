package com.example.demo;

import java.sql.Timestamp;

public class DiscussionReply {
    private int replyId;
    private int discussionId;
    private int userId;
    private Integer parentReplyId; // 可以为null
    private String content;
    private boolean isDeleted;
    private Timestamp createdAt;

    // 扩展字段
    private String username;

    // 默认构造函数
    public DiscussionReply() {
    }

    // 带参数的构造函数
    public DiscussionReply(int replyId, int discussionId, int userId, Integer parentReplyId,
                           String content, boolean isDeleted, Timestamp createdAt) {
        this.replyId = replyId;
        this.discussionId = discussionId;
        this.userId = userId;
        this.parentReplyId = parentReplyId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public int getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(int discussionId) {
        this.discussionId = discussionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getParentReplyId() {
        return parentReplyId;
    }

    public void setParentReplyId(Integer parentReplyId) {
        this.parentReplyId = parentReplyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "DiscussionReply [replyId=" + replyId + ", discussionId=" + discussionId + ", userId=" + userId
                + ", content=" + (content != null ? content.substring(0, Math.min(content.length(), 20)) + "..." : "null")
                + ", isDeleted=" + isDeleted + "]";
    }
}