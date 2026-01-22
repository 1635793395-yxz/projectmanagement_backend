package com.project.project_management.repository;

import com.project.project_management.entity.SysChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SysChatRepository extends JpaRepository<SysChat, Long> {

    // 1. 查询两人聊天记录
    @Query("SELECT c FROM SysChat c WHERE (c.senderId = ?1 AND c.receiverId = ?2) OR (c.senderId = ?2 AND c.receiverId = ?1) ORDER BY c.createdAt ASC")
    List<SysChat> findHistory(Long userId1, Long userId2);

    // 2. 统计总未读数
    @Query("SELECT COUNT(c) FROM SysChat c WHERE c.receiverId = ?1 AND c.isRead = false")
    Long countUnread(Long receiverId);

    // 3. 统计来自特定发送者的未读数 (下拉列表用)
    @Query("SELECT COUNT(c) FROM SysChat c WHERE c.senderId = ?1 AND c.receiverId = ?2 AND c.isRead = false")
    Long countUnreadFrom(Long senderId, Long myId);
}