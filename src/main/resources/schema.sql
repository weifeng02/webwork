-- ============================================================
-- 校园失物招领系统 - MySQL 数据库初始化脚本
-- ============================================================
-- 此脚本创建系统所需的4个核心表：用户表、失物表、招领表、认领申请表
-- 执行前请确保已创建数据库：CREATE DATABASE lostfound CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ============================================================

-- 使用 utf8mb4 字符集，支持完整的 Unicode 字符（包括中文表情）
SET NAMES utf8mb4;

-- ---------------------------------------------------------------
-- 用户表 (users)
-- 存储系统用户信息，支持普通用户和管理员两种角色
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    -- 用户唯一标识，使用 CHAR(36) 存储 UUID 字符串
    id CHAR(36) PRIMARY KEY,
    -- 用户名（登录账号），唯一且不可为空
    username VARCHAR(50) NOT NULL UNIQUE,
    -- 密码（生产环境应使用 BCrypt 等加密算法存储哈希值）
    password VARCHAR(255) NOT NULL,
    -- 电子邮箱
    email VARCHAR(100) NOT NULL,
    -- 手机号
    phone VARCHAR(20),
    -- 角色：user（普通用户）或 admin（管理员）
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    -- 创建时间，默认当前时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 为 username 和 email 添加索引，加速登录查询
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ---------------------------------------------------------------
-- 失物表 (lost_items)
-- 存储用户发布的丢失物品信息
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS lost_items (
    -- 失物记录唯一标识
    id CHAR(36) PRIMARY KEY,
    -- 发布者用户ID（外键关联 users 表）
    user_id CHAR(36),
    -- 物品名称
    item_name VARCHAR(100) NOT NULL,
    -- 物品描述
    description TEXT,
    -- 物品类别（如电子产品、证件、钱包、钥匙等）
    category VARCHAR(50) NOT NULL,
    -- 丢失地点
    location VARCHAR(100) NOT NULL,
    -- 丢失时间
    lost_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 物品图片的相对 URL 路径
    image_url VARCHAR(255),
    -- 状态：pending（待找回）、matched（已匹配）、found（已找回）
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    -- 记录创建时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 外键约束：关联用户表
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    -- 索引优化：按类别、地点、状态查询的场景
    INDEX idx_category (category),
    INDEX idx_location (location),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='失物表';

-- ---------------------------------------------------------------
-- 招领表 (found_items)
-- 存储用户发布的拾到物品信息
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS found_items (
    -- 招领记录唯一标识
    id CHAR(36) PRIMARY KEY,
    -- 发布者用户ID（外键关联 users 表）
    user_id CHAR(36),
    -- 物品名称
    item_name VARCHAR(100) NOT NULL,
    -- 物品描述
    description TEXT,
    -- 物品类别
    category VARCHAR(50) NOT NULL,
    -- 拾取地点
    location VARCHAR(100) NOT NULL,
    -- 拾取时间
    found_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 物品图片的相对 URL 路径
    image_url VARCHAR(255),
    -- 拾到者的联系方式（手机号或微信）
    contact VARCHAR(100) NOT NULL,
    -- 状态：pending（待认领）、returned（已归还）
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    -- 记录创建时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 外键约束：关联用户表
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    -- 索引优化
    INDEX idx_category (category),
    INDEX idx_location (location),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='招领表';

-- ---------------------------------------------------------------
-- 认领申请表 (claim_requests)
-- 存储用户提交的认领申请，管理员审核后更新状态
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS claim_requests (
    -- 申请记录唯一标识
    id CHAR(36) PRIMARY KEY,
    -- 关联的失物ID（外键关联 lost_items 表）
    lost_item_id CHAR(36),
    -- 关联的招领ID（外键关联 found_items 表）
    found_item_id CHAR(36),
    -- 申请人的用户ID（外键关联 users 表）
    user_id CHAR(36),
    -- 状态：pending（待审核）、approved（已通过）、rejected（已拒绝）
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    -- 申请说明/证明材料
    message TEXT,
    -- 申请创建时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 外键约束
    FOREIGN KEY (lost_item_id) REFERENCES lost_items(id) ON DELETE CASCADE,
    FOREIGN KEY (found_item_id) REFERENCES found_items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    -- 索引优化
    INDEX idx_status (status),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认领申请表';
