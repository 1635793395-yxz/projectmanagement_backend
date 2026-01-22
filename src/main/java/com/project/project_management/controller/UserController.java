package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.SysUser;
import com.project.project_management.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private SysUserRepository userRepo;

    // 1. 获取待审核的负责人列表
    @GetMapping("/pending")
    public Result<?> listPending() {
        return Result.success(userRepo.findByRoleHeavy("PENDING_MANAGER"));
    }

    // 2. 审核接口
    @PostMapping("/audit")
    public Result<?> audit(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String status = params.get("status").toString(); // APPROVED 或 REJECTED

        SysUser user = userRepo.findById(id).orElse(null);
        if (user == null) return Result.error("用户不存在");

        if ("APPROVED".equals(status)) {
            user.setRole("MANAGER"); // 转正为负责人
            userRepo.save(user);
            return Result.success("审核通过，已设为负责人");
        } else {
            // 驳回则直接删除该账号，让其重新注册
            userRepo.delete(user);
            return Result.success("已驳回并删除该申请");
        }
    }
}