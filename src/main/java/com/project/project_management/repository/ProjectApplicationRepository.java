package com.project.project_management.repository;

import com.project.project_management.entity.ProjectApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {
    // 查某个人的申请记录
    List<ProjectApplication> findByApplicantId(Long applicantId);

    // 查某种状态的申请 (给管理员用，比如查所有“待审核”的)
    List<ProjectApplication> findByStatus(String status);
}