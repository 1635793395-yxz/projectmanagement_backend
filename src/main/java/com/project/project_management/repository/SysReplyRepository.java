package com.project.project_management.repository;

import com.project.project_management.entity.SysReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SysReplyRepository extends JpaRepository<SysReply, Long> {
    // 查某条留言的所有回复
    List<SysReply> findByMessageId(Long messageId);
}