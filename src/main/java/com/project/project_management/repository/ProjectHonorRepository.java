package com.project.project_management.repository;

import com.project.project_management.entity.ProjectHonor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectHonorRepository extends JpaRepository<ProjectHonor, Long> {
    List<ProjectHonor> findByProjectId(Long projectId);

    // 查待审核的
    List<ProjectHonor> findByStatus(String status);
}