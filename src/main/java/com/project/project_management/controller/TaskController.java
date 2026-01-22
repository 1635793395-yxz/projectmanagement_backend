package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.ProjectInfo;
import com.project.project_management.entity.ProjectTask;
import com.project.project_management.repository.ProjectInfoRepository;
import com.project.project_management.repository.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private ProjectTaskRepository taskRepo;

    @Autowired
    private ProjectInfoRepository projectRepo;

    // 1. 获取某项目的任务列表
    @GetMapping
    public Result<?> list(@RequestParam Long projectId) {
        return Result.success(taskRepo.findByProjectId(projectId));
    }

    // 2. 新增任务
    @PostMapping
    public Result<?> add(@RequestBody ProjectTask task) {
        if (task.getContent() == null || task.getContent().isEmpty()) {
            return Result.error("任务内容不能为空");
        }
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        taskRepo.save(task);

        // 🔄 重新计算进度
        updateProjectProgress(task.getProjectId());

        return Result.success("任务添加成功");
    }

    // 3. 切换完成状态 (打钩/取消)
    @PostMapping("/toggle/{id}")
    public Result<?> toggle(@PathVariable Long id) {
        ProjectTask task = taskRepo.findById(id).orElse(null);
        if (task == null) return Result.error("任务不存在");

        task.setIsCompleted(!task.getIsCompleted()); // 取反
        taskRepo.save(task);

        // 🔄 重新计算进度
        updateProjectProgress(task.getProjectId());

        return Result.success("状态已更新");
    }

    // 4. 删除任务
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        ProjectTask task = taskRepo.findById(id).orElse(null);
        if (task != null) {
            Long projectId = task.getProjectId();
            taskRepo.delete(task);
            // 🔄 重新计算进度
            updateProjectProgress(projectId);
        }
        return Result.success("任务已删除");
    }

    /**
     * 🧠 核心算法：自动计算并更新项目进度
     */
    private void updateProjectProgress(Long projectId) {
        List<ProjectTask> tasks = taskRepo.findByProjectId(projectId);

        if (tasks.isEmpty()) {
            // 如果没任务，进度归0 (或者保持不变，看你需求)
            updateProgressInDb(projectId, 0);
            return;
        }

        // 计算公式：完成数 / 总数 * 100
        long completedCount = tasks.stream().filter(ProjectTask::getIsCompleted).count();
        int newProgress = (int) ((completedCount * 1.0 / tasks.size()) * 100);

        updateProgressInDb(projectId, newProgress);
    }

    private void updateProgressInDb(Long projectId, int progress) {
        ProjectInfo project = projectRepo.findById(projectId).orElse(null);
        if (project != null) {
            project.setProgress(progress);
            projectRepo.save(project);
            System.out.println("✅ 项目 [" + project.getName() + "] 进度自动更新为: " + progress + "%");
        }
    }
}