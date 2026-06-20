package com.lostfound.model;

import java.time.Instant;
import java.util.UUID;

/**
 * 用户实体类
 * 对应数据库表 users，用于存储系统用户信息
 * 支持普通用户（user）和管理员（admin）两种角色
 */
public class User {
    // 用户唯一标识（UUID 主键）
    private UUID id;
    // 用户名（登录账号）
    private String username;
    // 密码（生产环境应使用加密存储）
    private String password;
    // 电子邮箱
    private String email;
    // 手机号
    private String phone;
    // 角色：user（普通用户）或 admin（管理员）
    private String role;
    // 创建时间
    private Instant createdAt;

    public User() {}

    public User(UUID id, String username, String password, String email, String phone, String role, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /**
     * 判断当前用户是否为管理员
     * @return true 表示管理员角色
     */
    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
