package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.SysChat;
import com.project.project_management.entity.SysUser;
import com.project.project_management.repository.SysChatRepository;
import com.project.project_management.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private SysChatRepository chatRepo;
    @Autowired
    private SysUserRepository userRepo;

    // 1. 发送消息
    @PostMapping("/send")
    public Result<?> send(@RequestBody SysChat chat) {
        if (chat.getContent() == null || chat.getContent().isEmpty()) return Result.error("内容为空");
        chat.setCreatedAt(LocalDateTime.now());
        chat.setIsRead(false);
        chatRepo.save(chat);
        return Result.success("发送成功");
    }

    // 2. 获取历史
    @GetMapping("/history")
    public Result<?> history(@RequestParam Long userId1, @RequestParam Long userId2) {
        return Result.success(chatRepo.findHistory(userId1, userId2));
    }

    // 3. 标记已读
    @PostMapping("/read")
    public Result<?> markRead(@RequestBody SysChat params) {
        Long myId = params.getReceiverId();
        Long partnerId = params.getSenderId();
        List<SysChat> history = chatRepo.findHistory(myId, partnerId);
        for (SysChat chat : history) {
            if (chat.getReceiverId().equals(myId) && !chat.getIsRead()) {
                chat.setIsRead(true);
                chatRepo.save(chat);
            }
        }
        return Result.success("已读");
    }

    // 4. 获取联系人
    @GetMapping("/contacts")
    public Result<?> contacts(@RequestParam String myRole, @RequestParam(required = false) Long myId) {
        String targetRole = "ADMIN".equalsIgnoreCase(myRole) ? "MANAGER" : "ADMIN";

        // 1. 先查出所有人
        List<SysUser> users = userRepo.findByRoleHeavy(targetRole);

        // 2. 包装成 Map，附带未读数
        List<Map<String, Object>> contactList = new ArrayList<>();

        for (SysUser u : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("realName", u.getRealName());
            map.put("role", u.getRole());

            // 3. 查询这个人给我发了几条未读消息
            Long count = 0L;
            if (myId != null) {
                count = chatRepo.countUnreadFrom(u.getId(), myId);
            }
            map.put("unread", count); // 把未读数放进去

            contactList.add(map);
        }

        return Result.success(contactList);
    }

    // 5. 获取总未读数
    @GetMapping("/unread")
    public Result<?> unread(@RequestParam Long userId) {
        return Result.success(chatRepo.countUnread(userId));
    }
}