package com.project.project_management.repository;

import com.project.project_management.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    // 查某项目的所有任务
    List<ProjectTask> findByProjectId(Long projectId);
}