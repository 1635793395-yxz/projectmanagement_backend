-- 1. 用户表
CREATE TABLE sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    role VARCHAR(20) DEFAULT 'MEMBER', -- ADMIN, MANAGER, MEMBER, PENDING_MANAGER
    applying_project VARCHAR(255), -- 负责人注册时填写的项目名
    status INT DEFAULT 1,
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 项目信息表
CREATE TABLE project_info (
    id BIGSERIAL PRIMARY KEY,
    project_code VARCHAR(50) UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50), -- 软件开发/硬件集成
    intro TEXT, -- 公开简介
    details TEXT, -- 内部详情 (机密)
    internal_resources TEXT, -- 资源配置 (机密)
    manager_id BIGINT,
    manager_name VARCHAR(50),
    status VARCHAR(20) DEFAULT '筹备中', -- 筹备中, 进行中, 已暂停, 已完结
    progress INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 项目申请表 (立项申请)
CREATE TABLE project_application (
    id BIGSERIAL PRIMARY KEY,
    applicant_id BIGINT NOT NULL,
    project_name VARCHAR(100) NOT NULL,
    project_code VARCHAR(50),
    category VARCHAR(50),
    reason TEXT, -- 申请理由/简介
    details TEXT, -- 内部详情
    internal_resources TEXT,
    intro TEXT, -- 公开简介
    manager_name VARCHAR(50),
    material_url VARCHAR(255), -- 申请材料路径
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    reject_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. 团队成员表
CREATE TABLE project_member (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT, -- 如果是系统内用户
    member_name VARCHAR(50),
    role VARCHAR(20) DEFAULT 'MEMBER', -- MEMBER, MANAGER
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 任务表
CREATE TABLE project_task (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    content TEXT,
    is_completed BOOLEAN DEFAULT FALSE, -- PG 使用 BOOLEAN 类型
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. 荣誉表
CREATE TABLE project_honor (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    project_name VARCHAR(100),
    content VARCHAR(255),
    proof_image VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    reject_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. 留言表
CREATE TABLE sys_message (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    user_name VARCHAR(50),
    content TEXT,
    reply_content TEXT,
    replier_name VARCHAR(50),
    is_replied BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    replied_at TIMESTAMP
);

-- 初始化一个管理员账号 (密码自行加密，这里假设是明文或简单的加密字符串)
-- 注意：PG 的字符串必须用单引号
INSERT INTO sys_user (username, password, real_name, role) 
VALUES ('admin', '123456', '系统管理员', 'ADMIN');
