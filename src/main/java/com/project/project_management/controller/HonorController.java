package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.ProjectHonor;
import com.project.project_management.repository.ProjectHonorRepository;
import com.project.project_management.repository.ProjectInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/honors")
public class HonorController {

    @Autowired
    private ProjectHonorRepository honorRepo;

    @Autowired
    private ProjectInfoRepository projectRepo;

    // 1. 获取某项目的荣誉列表
    @GetMapping
    public Result<?> list(@RequestParam Long projectId) {
        return Result.success(honorRepo.findByProjectId(projectId));
    }

    // 2. 获取所有待审核荣誉 (管理员用)
    @GetMapping("/pending")
    public Result<?> listPending() {
        List<ProjectHonor> list = honorRepo.findByStatus("PENDING");
        // 填充项目名
        list.forEach(h ->
                projectRepo.findById(h.getProjectId()).ifPresent(p -> h.setProjectName(p.getName()))
        );
        return Result.success(list);
    }

    // 3. 新增荣誉 / 修改被驳回的荣誉
    @PostMapping
    public Result<?> save(@RequestBody ProjectHonor honor) {
        if (honor.getContent() == null) return Result.error("内容不能为空");

        // 如果是修改(带ID)，重置状态为待审核
        if (honor.getId() != null) {
            ProjectHonor existing = honorRepo.findById(honor.getId()).orElse(null);
            if (existing != null) {
                existing.setContent(honor.getContent());
                existing.setProofImage(honor.getProofImage());
                existing.setStatus("PENDING"); // 修改后重新提交审核
                existing.setRejectReason(null); // 清空驳回理由
                honorRepo.save(existing);
                return Result.success("已重新提交审核");
            }
        }

        // 新增
        honor.setStatus("PENDING");
        honor.setCreatedAt(LocalDateTime.now());
        honorRepo.save(honor);
        return Result.success("荣誉已提交，等待审核");
    }

    // 4. 审核接口
    @PostMapping("/audit")
    public Result<?> audit(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String status = params.get("status").toString();
        String reason = params.get("reason") != null ? params.get("reason").toString() : "";

        ProjectHonor honor = honorRepo.findById(id).orElse(null);
        if (honor == null) return Result.error("记录不存在");

        honor.setStatus(status);
        if ("REJECTED".equals(status)) {
            honor.setRejectReason(reason);
        }
        honorRepo.save(honor);
        return Result.success("审核完成");
    }

    // 5. 删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        honorRepo.deleteById(id);
        return Result.success("删除成功");
    }
}