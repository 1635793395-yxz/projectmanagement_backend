package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.ProjectInfo;
import com.project.project_management.entity.ProjectMember;
import com.project.project_management.entity.SysUser;
import com.project.project_management.repository.ProjectInfoRepository;
import com.project.project_management.repository.ProjectMemberRepository;
import com.project.project_management.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    // 列表接口
    @GetMapping
    public Result<?> list() {
        List<ProjectInfo> list = projectInfoRepository.findAll();

        // 遍历列表，把 ID 翻译成 名字
        for (ProjectInfo project : list) {
            if (project.getManagerId() != null) {
                // 去用户表查名字
                sysUserRepository.findById(project.getManagerId())
                        .ifPresent(user -> project.setManagerName(user.getRealName()));
            }
            // 列表页也不允许查看机密信息，防止通过列表接口泄露
            project.setDetails(null);
            project.setInternalResources(null);
        }

        return Result.success(list);
    }

    // 详情接口
    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id, @RequestParam Long userId) {
        // 1. 查项目
        Optional<ProjectInfo> projectOpt = projectInfoRepository.findById(id);
        if (projectOpt.isEmpty()) return Result.error("项目不存在");
        ProjectInfo project = projectOpt.get();

        // 2. 把负责人 ID 翻译成名字
        if (project.getManagerId() != null) {
            sysUserRepository.findById(project.getManagerId())
                    .ifPresent(manager -> project.setManagerName(manager.getRealName()));
        }

        // 3. 查当前登录用户（为了判断权限）
        SysUser user = sysUserRepository.findById(userId).orElse(new SysUser());
        // 这里判断是否是 Owner 时，要用数据库里的 ID 判断，不要用名字
        boolean isOwner = user.getId() != null && user.getId().equals(project.getManagerId());

        // 4. 权限脱敏
        if (!isOwner) {
            project.setDetails(null);
            project.setInternalResources(null);
        }

        return Result.success(project);
    }

    // 强力防爆版的新增接口
    @PostMapping
    public Result<?> add(@RequestBody ProjectInfo project) {
        try {
            // 1. 打印日志：看看前端到底传了啥
            System.out.println("收到新项目请求：" + project);
            System.out.println("负责人名字：" + project.getManagerName());

            // 2. 校验
            if (project.getName() == null || project.getName().isEmpty()) {
                return Result.error("名称不能为空");
            }
            if (project.getProjectCode() == null || project.getProjectCode().isEmpty()) {
                return Result.error("编号不能为空");
            }

            // 3. 处理负责人 (带空指针保护)
            String inputName = project.getManagerName();
            if (inputName != null && !inputName.trim().isEmpty()) {
                SysUser manager = sysUserRepository.findByRealName(inputName);
                if (manager == null) {
                    // 自动注册逻辑
                    manager = new SysUser();
                    manager.setRealName(inputName);
                    manager.setUsername("user_" + System.currentTimeMillis());
                    manager.setPassword("123456");
                    manager.setRole("MANAGER");
                    manager.setStatus(1);
                    manager.setCreatedAt(LocalDateTime.now());
                    manager.setUpdatedAt(LocalDateTime.now());
                    sysUserRepository.save(manager);
                }
                project.setManagerId(manager.getId());
            } else {
                // 如果没名字，暂时填个 1 (保底)，或者报错
                // return Result.error("请填写负责人姓名");
                project.setManagerId(1L);
            }

            // 4. 默认值
            if (project.getStatus() == null) project.setStatus("筹备中");
            if (project.getProgress() == null) project.setProgress(0);
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());

            // 5. 保存
            projectInfoRepository.save(project);
            return Result.success("创建成功");

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 🚨 只有真正的“完整性冲突”（如编号重复）才报这个错
            // 打印一下具体是哪个字段冲突了
            System.err.println("数据冲突错误: " + e.getMessage());
            return Result.error("提交失败：项目编号可能重复，请检查！");
        } catch (Exception e) {
            // 🚨 其他所有错误（比如空指针、逻辑错误），都走这里
            e.printStackTrace(); // 在 IDEA 控制台打印堆栈，这很关键！
            return Result.error("服务器内部错误：" + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
    // 删除项目接口
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            // 检查是否存在
            if (!projectInfoRepository.existsById(id)) {
                return Result.error("项目不存在，可能已被删除");
            }

            // 执行删除
            projectInfoRepository.deleteById(id);

            return Result.success("项目删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败，请检查该项目是否还有关联数据");
        }
    }

    // 更新项目信息接口
    @PutMapping
    public Result<?> update(@RequestBody ProjectInfo project) {
        // 1. 先查一下原来的数据
        ProjectInfo oldProject = projectInfoRepository.findById(project.getId()).orElse(null);
        if (oldProject == null) {
            return Result.error("项目不存在");
        }

        // 2. 更新允许修改的字段
        oldProject.setName(project.getName());
        oldProject.setCategory(project.getCategory());
        oldProject.setIntro(project.getIntro());
        oldProject.setDetails(project.getDetails());
        oldProject.setInternalResources(project.getInternalResources());

        // 核心：更新进度和状态
        oldProject.setProgress(project.getProgress());
        oldProject.setStatus(project.getStatus());

        oldProject.setUpdatedAt(LocalDateTime.now()); // 更新时间

        // 3. 保存
        projectInfoRepository.save(oldProject);
        return Result.success("项目信息更新成功！");
    }

    @GetMapping("/my")
    public Result<?> getMyProjects(@RequestParam Long userId) {
        // 1. 先找出这个用户参与的所有记录
        List<ProjectMember> memberships = projectMemberRepository.findByUserId(userId);

        // 2. 提取出所有的项目 ID
        List<Long> projectIds = memberships.stream()
                .map(ProjectMember::getProjectId)
                .collect(Collectors.toList());

        // 3. 还要加上“我是负责人”的项目 (因为负责人不一定在 member 表里)
        List<ProjectInfo> managedProjects = projectInfoRepository.findByManagerId(userId);
        for (ProjectInfo p : managedProjects) {
            if (!projectIds.contains(p.getId())) {
                projectIds.add(p.getId());
            }
        }

        if (projectIds.isEmpty()) {
            return Result.success(List.of()); // 返回空列表
        }

        // 4. 去项目表里查详细信息
        List<ProjectInfo> myProjects = projectInfoRepository.findAllById(projectIds);

        // 5. 补全负责人名字
        for (ProjectInfo project : myProjects) {
            sysUserRepository.findById(project.getManagerId()).ifPresent(u -> project.setManagerName(u.getRealName()));

            project.setDetails(null);
            project.setInternalResources(null);
        }

        return Result.success(myProjects);
    }
}