package com.project.project_management.repository;

import com.project.project_management.entity.ProjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectInfoRepository extends JpaRepository<ProjectInfo, Long> {
    // 继承了 JpaRepository，自动拥有 findAll(), save() 等方法
    List<ProjectInfo> findByManagerId(Long managerId);
}