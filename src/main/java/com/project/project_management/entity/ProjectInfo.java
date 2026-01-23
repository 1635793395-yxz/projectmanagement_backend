package com.project.project_management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.persistence.Transient;

@Entity
@Table(name = "project_info")
public class ProjectInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_code", columnDefinition = "TEXT")
    private String projectCode; // 项目编号

    private String name;        // 项目名称
    private String category;    // 类别
    private String status;      // 状态: IN_PROGRESS(进行中), COMPLETED(已结项)等

    @Column(name = "manager_id", columnDefinition = "TEXT")
    private Long managerId;     // 负责人ID (为了简单，这里直接存ID)

    private Integer progress;   // 进度 0-100
    private String intro;       // 公开简介

    // ⚠️ 注意：文档说下面这俩字段对普通用户不可见，我们在 Controller 里会处理它
    private String details;           // 内部详情
    @Column(name = "internal_resources", columnDefinition = "TEXT")
    private String internalResources; // 内部资源

    @Column(name = "created_at", columnDefinition = "TEXT")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TEXT")
    private LocalDateTime updatedAt;

    @Transient // 这个字段只在代码里用，不存入数据库表
    private String managerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getInternalResources() {
        return internalResources;
    }

    public void setInternalResources(String internalResources) {
        this.internalResources = internalResources;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
