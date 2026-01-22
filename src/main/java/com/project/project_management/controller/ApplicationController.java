package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.ProjectApplication;
import com.project.project_management.entity.ProjectInfo;
import com.project.project_management.entity.SysUser;
import com.project.project_management.repository.ProjectApplicationRepository;
import com.project.project_management.repository.ProjectInfoRepository;
import com.project.project_management.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ProjectApplicationRepository applicationRepo;

    @Autowired
    private ProjectInfoRepository projectInfoRepo;

    @Autowired
    private SysUserRepository sysUserRepository;

    // 1. 提交申请 (现在接收的是完整的详细信息)
    @PostMapping("/submit")
    public Result<?> submit(@RequestBody ProjectApplication app) {
        if (app.getProjectName() == null || app.getProjectName().isEmpty()) {
            return Result.error("项目名称不能为空");
        }

        // 自动补充信息
        app.setStatus("PENDING");
        app.setCreatedAt(LocalDateTime.now());

        applicationRepo.save(app);
        return Result.success("详细申请已提交，等待管理员审核");
    }

    // 2. 获取列表 (保持不变)
    @GetMapping("/list")
    public Result<?> list(@RequestParam Long userId, @RequestParam String role) {
        if ("ADMIN".equals(role)) {
            return Result.success(applicationRepo.findByStatus("PENDING"));
        } else {
            return Result.success(applicationRepo.findByApplicantId(userId));
        }
    }

    // 3. 管理员审核 (核心逻辑：搬运数据)
    @PostMapping("/audit")
    public Result<?> audit(@RequestBody Map<String, Object> params) {
        // 1. 解析参数 (使用 Map 以便获取 reason)
        if (params.get("id") == null || params.get("status") == null) {
            return Result.error("参数缺失");
        }
        Long id = Long.valueOf(params.get("id").toString());
        String status = params.get("status").toString();

        ProjectApplication app = applicationRepo.findById(id).orElse(null);
        if (app == null) return Result.error("申请不存在");

        // 2. 更新申请状态
        app.setStatus(status);

        // 3. 分支处理
        if ("APPROVED".equals(status)) {
            // ✅【审核通过】：执行你提供的“创建项目”逻辑
            ProjectInfo newProject = new ProjectInfo();
            // 复制字段
            newProject.setName(app.getProjectName());
            newProject.setProjectCode(app.getProjectCode());
            newProject.setCategory(app.getCategory());
            newProject.setIntro(app.getIntro()); // 对应申请里的 reason/intro
            newProject.setDetails(app.getDetails());
            newProject.setInternalResources(app.getInternalResources());
            newProject.setManagerName(app.getManagerName());

            // 处理负责人ID (保留你的查找逻辑)
            if (app.getManagerName() != null) {
                SysUser manager = sysUserRepository.findByRealName(app.getManagerName()); // 需在 Repo 加此方法
                if (manager != null) {
                    newProject.setManagerId(manager.getId());
                } else {
                    newProject.setManagerId(app.getApplicantId()); // 兜底
                }
            } else {
                newProject.setManagerId(app.getApplicantId());
            }

            // 设置初始状态
            newProject.setStatus("筹备中");
            newProject.setProgress(0);
            newProject.setCreatedAt(LocalDateTime.now());
            newProject.setUpdatedAt(LocalDateTime.now());

            projectInfoRepo.save(newProject);

        } else if ("REJECTED".equals(status)) {
            // 保存驳回理由
            if (params.containsKey("reason")) {
                app.setRejectReason(params.get("reason").toString());
            }
        }

        // 4. 保存申请记录变更
        applicationRepo.save(app);
        return Result.success("审核完成");
    }

    // 4. 删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        applicationRepo.deleteById(id);
        return Result.success("删除成功");
    }
}