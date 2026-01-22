-- 设置字符集，防止乱码
SET NAMES utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS project_management_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE project_management_db;

-- ==========================================
-- 1. 用户与权限模块 (sys_user)
-- ==========================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '加密后的密码',
  `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `role` VARCHAR(20) NOT NULL COMMENT '角色标识: ADMIN, MANAGER, MEMBER, VISITOR',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='系统用户表';

-- ==========================================
-- 2. 项目管理模块 (project_info)
-- ==========================================
DROP TABLE IF EXISTS `project_info`;
CREATE TABLE `project_info` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '项目ID',
  `project_code` VARCHAR(50) UNIQUE COMMENT '项目编号',
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `category` VARCHAR(50) COMMENT '项目类别',
  `status` VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '项目状态',
  `manager_id` BIGINT COMMENT '项目负责人ID',
  `progress` INT DEFAULT 0 COMMENT '项目进度百分比',
  `intro` VARCHAR(500) COMMENT '项目简介(公开信息)',
  `details` TEXT COMMENT '项目详情',
  `internal_resources` TEXT COMMENT '内部资源配置',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='项目信息主表';

-- ==========================================
-- 3. 审批流程模块 (project_application)
-- ==========================================
DROP TABLE IF EXISTS `project_application`;
CREATE TABLE `project_application` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
  `project_name` VARCHAR(100) NOT NULL COMMENT '申请项目名称',
  `project_type` VARCHAR(50) COMMENT '项目类型',
  `reason` TEXT COMMENT '申请理由',
  `target` TEXT COMMENT '预期目标',
  `cycle` VARCHAR(100) COMMENT '实施周期',
  `resources` TEXT COMMENT '所需资源',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '审核状态',
  `reject_reason` VARCHAR(255) COMMENT '驳回原因',
  `audit_time` DATETIME COMMENT '审核时间',
  `auditor_id` BIGINT COMMENT '审核人ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='项目申请记录表';

-- ==========================================
-- 4. 互动与服务模块 (guest_message)
-- ==========================================
DROP TABLE IF EXISTS `guest_message`;
CREATE TABLE `guest_message` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '留言ID',
  `user_id` BIGINT COMMENT '留言用户ID',
  `project_id` BIGINT COMMENT '关联项目ID',
  `content` TEXT NOT NULL COMMENT '留言内容',
  `contact_info` VARCHAR(100) COMMENT '联系方式',
  `attachment_url` VARCHAR(255) COMMENT '图片附件地址',
  `is_replied` TINYINT DEFAULT 0 COMMENT '是否已回复',
  `reply_content` TEXT COMMENT '管理员回复内容',
  `reply_user_id` BIGINT COMMENT '回复人ID',
  `reply_time` DATETIME COMMENT '回复时间',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='企服留言板表';

-- ==========================================
-- 5. 日志与辅助模块 (sys_log, search_history)
-- ==========================================
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT COMMENT '操作人ID',
  `module` VARCHAR(50) COMMENT '操作模块',
  `operation` VARCHAR(50) COMMENT '操作类型',
  `content` TEXT COMMENT '操作详情',
  `ip_address` VARCHAR(50) COMMENT '操作IP地址',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='系统操作日志表';

DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '查询关键词',
  `search_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '查询时间'
) COMMENT='用户查询历史表';