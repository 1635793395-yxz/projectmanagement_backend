package com.project.project_management.repository;

import com.project.project_management.entity.SysMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysMessageRepository extends JpaRepository<SysMessage, Long> {
    // 按时间倒序查询所有留言（最新的在前面）
    List<SysMessage> findAllByOrderByCreatedAtDesc();
}