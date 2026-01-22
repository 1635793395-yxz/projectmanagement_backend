package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.ProjectMember;
import com.project.project_management.entity.SysUser;
import com.project.project_management.repository.ProjectMemberRepository;
import com.project.project_management.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private ProjectMemberRepository memberRepo;

    @Autowired
    private SysUserRepository userRepo;

    // 1. 获取项目成员列表
    @GetMapping
    public Result<?> list(@RequestParam Long projectId) {
        List<ProjectMember> members = memberRepo.findByProjectId(projectId);

        // 补全名字信息
        for (ProjectMember pm : members) {
            userRepo.findById(pm.getUserId()).ifPresent(u -> {
                pm.setMemberName(u.getRealName());
                pm.setRole(u.getRole());
            });
        }
        return Result.success(members);
    }

    // 2. 添加成员 (根据真实姓名添加)
    @PostMapping("/add")
    public Result<?> add(@RequestBody AddMemberRequest req) {
        // 2.1 先找人
        SysUser user = userRepo.findByRealName(req.getRealName());
        if (user == null) {
            return Result.error("找不到该用户: " + req.getRealName());
        }

        // 2.2 检查是否已存在
        ProjectMember exist = memberRepo.findByProjectIdAndUserId(req.getProjectId(), user.getId());
        if (exist != null) {
            return Result.error("该用户已经是项目成员了");
        }

        // 2.3 入库
        ProjectMember pm = new ProjectMember();
        pm.setProjectId(req.getProjectId());
        pm.setUserId(user.getId());
        pm.setJoinedAt(LocalDateTime.now());

        memberRepo.save(pm);
        return Result.success("成员添加成功");
    }

    // 3. 移除成员
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        memberRepo.deleteById(id);
        return Result.success("成员已移除");
    }

    // 内部类：接收前端参数
    public static class AddMemberRequest {
        private Long projectId;
        private String realName;
        // Getters & Setters
        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
    }
}