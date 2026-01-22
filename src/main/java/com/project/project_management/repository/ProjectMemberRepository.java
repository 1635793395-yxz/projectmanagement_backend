package com.project.project_management.repository;

import com.project.project_management.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    // 1. 查项目的所有成员
    List<ProjectMember> findByProjectId(Long projectId);

    // 2. 查某人是否在某项目里
    ProjectMember findByProjectIdAndUserId(Long projectId, Long userId);

    // 3. 查某人参与的所有项目
    List<ProjectMember> findByUserId(Long userId);
}