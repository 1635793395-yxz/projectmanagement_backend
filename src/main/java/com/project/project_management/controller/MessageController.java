package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.SysMessage;
import com.project.project_management.entity.SysReply;
import com.project.project_management.repository.ProjectInfoRepository;
import com.project.project_management.repository.SysMessageRepository;
import com.project.project_management.repository.SysReplyRepository;
import com.project.project_management.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private SysMessageRepository messageRepo;
    @Autowired
    private SysReplyRepository replyRepo;
    @Autowired
    private SysUserRepository userRepo;
    @Autowired
    private ProjectInfoRepository projectRepo;

    // 1. 获取留言列表 (包含回复列表)
    @GetMapping("/list")
    public Result<?> list() {
        List<SysMessage> list = messageRepo.findAllByOrderByCreatedAtDesc();

        for (SysMessage msg : list) {
            // 填充名字
            userRepo.findById(msg.getUserId()).ifPresent(u -> msg.setUserName(u.getRealName()));
            if (msg.getProjectId() != null) {
                projectRepo.findById(msg.getProjectId()).ifPresent(p -> msg.setProjectName(p.getName()));
            }
            // 填充回复列表
            List<SysReply> replies = replyRepo.findByMessageId(msg.getId());
            msg.setReplyList(replies);
            // 只要有回复记录，就算已回复
            msg.setIsReplied(!replies.isEmpty());
        }
        return Result.success(list);
    }

    // 2. 提交留言
    @PostMapping("/add")
    public Result<?> add(@RequestBody SysMessage msg) {
        if (msg.getContent() == null) return Result.error("内容为空");
        msg.setCreatedAt(LocalDateTime.now());
        msg.setIsReplied(false);
        messageRepo.save(msg);
        return Result.success("留言提交成功");
    }

    // 3. 将回复插入到 sys_reply 表
    @PostMapping("/reply")
    public Result<?> reply(@RequestBody Map<String, Object> params) {
        Long messageId = Long.valueOf(params.get("id").toString());
        String content = params.get("replyContent").toString();
        String replierName = params.get("replierName").toString();
        // 获取 replierId (为了安全建议传，这里假设前端会传)
        Long replierId = params.get("replierId") != null ? Long.valueOf(params.get("replierId").toString()) : 0L;

        SysMessage msg = messageRepo.findById(messageId).orElse(null);
        if (msg == null) return Result.error("留言不存在");

        // 创建新的回复记录
        SysReply reply = new SysReply();
        reply.setMessageId(messageId);
        reply.setContent(content);
        reply.setReplierName(replierName);
        reply.setReplierId(replierId);
        reply.setCreatedAt(LocalDateTime.now());

        replyRepo.save(reply);

        // 更新主表状态
        msg.setIsReplied(true);
        // 为了兼容旧数据，可以更新一下最后回复信息（可选）
        msg.setReplyContent(content);
        msg.setReplyTime(LocalDateTime.now());
        msg.setReplierName(replierName);
        messageRepo.save(msg);

        return Result.success("回复成功");
    }

    // 4. 删除留言
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        // 先删回复
        List<SysReply> replies = replyRepo.findByMessageId(id);
        replyRepo.deleteAll(replies);
        // 再删留言
        messageRepo.deleteById(id);
        return Result.success("留言及回复已删除");
    }

    // 删除单条回复
    @DeleteMapping("/reply/{replyId}")
    public Result<?> deleteReply(@PathVariable Long replyId) {
        replyRepo.deleteById(replyId);
        return Result.success("回复已删除");
    }
}