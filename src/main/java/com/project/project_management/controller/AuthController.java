package com.project.project_management.controller;

import com.project.project_management.common.Result;
import com.project.project_management.entity.SysUser;
import com.project.project_management.repository.SysUserRepository; //
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserRepository sysUserRepository;

    // 1. 登录接口
    @PostMapping("/login")
    // 增加 HttpServletRequest 参数来获取 Session
    public Result<?> login(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String captcha = (String) params.get("captcha"); // 获取前端传来的验证码

        // 验证码校验逻辑
        HttpSession session = request.getSession();
        String correctCode = (String) session.getAttribute("CAPTCHA_CODE");

        if (correctCode == null || captcha == null || !correctCode.equalsIgnoreCase(captcha)) {
            return Result.error("验证码错误");
        }
        // 校验通过后，移除Session里的验证码（防止重复使用）
        session.removeAttribute("CAPTCHA_CODE");

        // ... 原有的登录逻辑 (查数据库、比对密码等) ...
        SysUser user = sysUserRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(user);
    }

    // 2. 注册接口
    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String captcha = (String) params.get("captcha");
        HttpSession session = request.getSession();
        String correctCode = (String) session.getAttribute("CAPTCHA_CODE");

        if (correctCode == null || captcha == null || !correctCode.equalsIgnoreCase(captcha)) {
            return Result.error("验证码错误");
        }
        session.removeAttribute("CAPTCHA_CODE");
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String realName = (String) params.get("realName");
        String regType = (String) params.get("regType");
        String applyingProject = (String) params.get("applyingProject");

        if (sysUserRepository.findByUsername(username) != null) {
            return Result.error("用户名已存在");
        }
        SysUser newUser = new SysUser();
        newUser.setUsername(username);
        newUser.setPassword(password); // 实际生产环境请加密
        newUser.setRealName(realName);
        newUser.setCreatedAt(java.time.LocalDateTime.now());
        newUser.setStatus(1);
        newUser.setRole("MEMBER"); // 默认角色
        sysUserRepository.save(newUser);

        if ("MANAGER".equals(regType)) {
            newUser.setRole("PENDING_MANAGER");
            newUser.setApplyingProject(applyingProject);
        } else {
            newUser.setRole("MEMBER");
        }

        sysUserRepository.save(newUser);

        if ("MANAGER".equals(regType)) {
            return Result.success("注册申请已提交，请等待管理员审核");
        } else {
            return Result.success("注册成功");
        }
    }
}