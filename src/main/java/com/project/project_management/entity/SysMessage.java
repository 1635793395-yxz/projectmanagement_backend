package com.project.project_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_message")
public class SysMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "project_id")
    private Long projectId;

    private String content;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "reply_content")
    private String replyContent;

    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    @Column(name = "is_replied")
    private Boolean isReplied = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "replier_name")
    private String replierName;

    // 临时字段：用于前端显示留言人名字、关联项目名
    @Transient
    private String userName;

    @Transient
    private String projectName;

    @Transient
    private java.util.List<SysReply> replyList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public LocalDateTime getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(LocalDateTime replyTime) {
        this.replyTime = replyTime;
    }

    public Boolean getReplied() {
        return isReplied;
    }

    public void setReplied(Boolean replied) {
        isReplied = replied;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getIsReplied() {
        return isReplied;
    }

    public void setIsReplied(Boolean isReplied) {
        this.isReplied = isReplied;
    }

    public String getReplierName() {
        return replierName;
    }

    public void setReplierName(String replierName) {
        this.replierName = replierName;
    }

    public java.util.List<SysReply> getReplyList() {
        return replyList;
    }

    public void setReplyList(java.util.List<SysReply> replyList) {
        this.replyList = replyList;
    }
}