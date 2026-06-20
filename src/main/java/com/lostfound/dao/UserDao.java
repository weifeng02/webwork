package com.lostfound.dao;

import com.lostfound.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用户数据访问对象（DAO）
 * 负责用户表的增删改查操作，使用 JDBC 连接 MySQL 数据库
 */
public class UserDao {

    /**
     * 插入一条新用户记录
     * @param user 要插入的用户对象（包含 UUID 主键、用户名、密码等）
     * @throws SQLException 数据库操作异常
     */
    public void insert(User user) throws SQLException {
        // SQL 插入语句，使用 ? 占位符防止 SQL 注入
        String sql = "INSERT INTO users (id, username, password, email, phone, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); // 获取数据库连接
             PreparedStatement ps = conn.prepareStatement(sql)) { // 预编译 SQL
            // 设置参数：UUID 转为字符串存储（MySQL 字符类型字段）
            ps.setString(1, user.getId() != null ? user.getId().toString() : null);
            ps.setString(2, user.getUsername());   // 用户名
            ps.setString(3, user.getPassword());   // 密码
            ps.setString(4, user.getEmail());      // 邮箱
            ps.setString(5, user.getPhone());      // 手机号
            ps.setString(6, user.getRole());       // 角色
            ps.setTimestamp(7, user.getCreatedAt() != null ? Timestamp.from(user.getCreatedAt()) : null); // 创建时间
            ps.executeUpdate(); // 执行插入
        }
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 找到的用户对象，未找到返回 null
     * @throws SQLException 数据库操作异常
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { // 执行查询
                if (rs.next()) { // 如果存在结果
                    return mapUser(rs); // 将结果集映射为 User 对象
                }
            }
        }
        return null;
    }

    /**
     * 根据 ID 查找用户
     * @param id 用户 UUID
     * @return 找到的用户对象，未找到返回 null
     * @throws SQLException 数据库操作异常
     */
    public User findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString()); // UUID 转为字符串查询
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * 查询所有用户（按创建时间降序）
     * @return 用户列表
     * @throws SQLException 数据库操作异常
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { // 遍历结果集
                list.add(mapUser(rs));
            }
        }
        return list;
    }

    /**
     * 更新用户信息
     * @param user 包含更新后的用户信息
     * @throws SQLException 数据库操作异常
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, email=?, phone=?, role=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getId().toString());
            ps.executeUpdate();
        }
    }

    /**
     * 根据 ID 删除用户
     * @param id 用户 UUID
     * @throws SQLException 数据库操作异常
     */
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    /**
     * 将 ResultSet 当前行映射为 User 对象
     * @param rs JDBC 结果集
     * @return 映射后的 User 对象
     * @throws SQLException 结果集读取异常
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        // 从数据库读取字符串转为 UUID
        String idStr = rs.getString("id");
        if (idStr != null) user.setId(UUID.fromString(idStr));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) user.setCreatedAt(ts.toInstant()); // Timestamp 转 Instant
        return user;
    }
}
