package com.project.project_management.repository;

import com.project.project_management.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    // 1. 根据用户名查找
    SysUser findByUsername(String username);

    // 2. 根据真实姓名查找 (之前的需求)
    SysUser findByRealName(String realName);

    // 3. 角色查找方法
    @Query(value = "SELECT * FROM sys_user WHERE UPPER(TRIM(role)) = UPPER(TRIM(:role))", nativeQuery = true)
    List<SysUser> findByRoleHeavy(@Param("role") String role);
}