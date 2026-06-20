package com.lostfound.dao;

import com.lostfound.model.FoundItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 招领数据访问对象（DAO）
 * 负责招领表（found_items）的增删改查操作，使用 JDBC 连接 MySQL 数据库
 * 支持按条件筛选、全文搜索和模糊查询功能
 */
public class FoundItemDao {

    /**
     * 插入一条新的招领记录
     * @param item 要插入的招领对象（包含物品名称、描述、类别、地点、联系方式等）
     * @throws SQLException 数据库操作异常
     */
    public void insert(FoundItem item) throws SQLException {
        String sql = "INSERT INTO found_items (id, user_id, item_name, description, category, location, found_time, image_url, contact, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); // 获取数据库连接
             PreparedStatement ps = conn.prepareStatement(sql)) { // 预编译 SQL 防注入
            ps.setString(1, item.getId() != null ? item.getId().toString() : null); // UUID 转字符串
            ps.setString(2, item.getUserId() != null ? item.getUserId().toString() : null); // 发布者用户ID
            ps.setString(3, item.getItemName());    // 物品名称
            ps.setString(4, item.getDescription()); // 物品描述
            ps.setString(5, item.getCategory());      // 物品类别
            ps.setString(6, item.getLocation());      // 拾取地点
            ps.setTimestamp(7, item.getFoundTime() != null ? Timestamp.from(item.getFoundTime()) : null); // 拾取时间
            ps.setString(8, item.getImageUrl());      // 图片URL
            ps.setString(9, item.getContact());      // 联系方式
            ps.setString(10, item.getStatus());      // 状态
            ps.setTimestamp(11, item.getCreatedAt() != null ? Timestamp.from(item.getCreatedAt()) : null); // 创建时间
            ps.executeUpdate(); // 执行插入操作
        }
    }

    /**
     * 根据 ID 查询招领详情
     * @param id 招领记录 UUID
     * @return 招领对象，未找到返回 null
     * @throws SQLException 数据库操作异常
     */
    public FoundItem findById(UUID id) throws SQLException {
        // LEFT JOIN 联表查询用户名称，用于展示发布者信息
        String sql = "SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id WHERE f.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString()); // UUID 转字符串
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs); // 映射结果集为对象
                }
            }
        }
        return null;
    }

    /**
     * 查询所有招领记录（按创建时间降序排列）
     * @return 招领列表
     * @throws SQLException 数据库操作异常
     */
    public List<FoundItem> findAll() throws SQLException {
        String sql = "SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id ORDER BY f.created_at DESC";
        List<FoundItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { // 遍历结果集
                list.add(mapItem(rs));
            }
        }
        return list;
    }

    /**
     * 按类别查询招领记录
     * @param category 物品类别（如电子产品、证件等）
     * @return 符合条件的招领列表
     * @throws SQLException 数据库操作异常
     */
    public List<FoundItem> findByCategory(String category) throws SQLException {
        String sql = "SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id WHERE f.category = ? ORDER BY f.created_at DESC";
        List<FoundItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    /**
     * 按地点模糊查询招领记录
     * @param location 地点关键词（如图书馆、食堂等）
     * @return 地点包含关键词的招领列表
     * @throws SQLException 数据库操作异常
     */
    public List<FoundItem> findByLocation(String location) throws SQLException {
        // MySQL 使用 LIKE 进行模糊匹配（默认 utf8mb4_general_ci 不区分大小写）
        String sql = "SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id WHERE f.location LIKE ? ORDER BY f.created_at DESC";
        List<FoundItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + location + "%"); // 前后加 % 实现模糊匹配
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    /**
     * 按状态查询招领记录
     * @param status 状态值（pending、returned）
     * @return 符合条件的招领列表
     * @throws SQLException 数据库操作异常
     */
    public List<FoundItem> findByStatus(String status) throws SQLException {
        String sql = "SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id WHERE f.status = ? ORDER BY f.created_at DESC";
        List<FoundItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    /**
     * 按用户 ID 查询该用户发布的所有招领
     * @param userId 用户 UUID
     * @return 该用户的招领列表
     * @throws SQLException 数据库操作异常
     */
    public List<FoundItem> findByUserId(UUID userId) throws SQLException {
        String sql = "SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id WHERE f.user_id = ? ORDER BY f.created_at DESC";
        List<FoundItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    /**
     * 更新招领记录
     * @param item 包含更新后的招领信息
     * @throws SQLException 数据库操作异常
     */
    public void update(FoundItem item) throws SQLException {
        String sql = "UPDATE found_items SET item_name=?, description=?, category=?, location=?, found_time=?, image_url=?, contact=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setString(2, item.getDescription());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getLocation());
            ps.setTimestamp(5, item.getFoundTime() != null ? Timestamp.from(item.getFoundTime()) : null);
            ps.setString(6, item.getImageUrl());
            ps.setString(7, item.getContact());
            ps.setString(8, item.getStatus());
            ps.setString(9, item.getId().toString());
            ps.executeUpdate();
        }
    }

    /**
     * 根据 ID 删除招领记录
     * @param id 招领 UUID
     * @throws SQLException 数据库操作异常
     */
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM found_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    /**
     * 全文关键词搜索招领记录
     * 在物品名称、描述、类别、地点中查找包含关键词的记录
     * @param keyword 搜索关键词
     * @return 匹配的招领列表
     * @throws SQLException 数据库操作异常
     */
    public List<FoundItem> search(String keyword) throws SQLException {
        // MySQL 使用 LIKE 进行模糊匹配，匹配多个字段
        String sql = "SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id " +
                "WHERE f.item_name LIKE ? OR f.description LIKE ? OR f.category LIKE ? OR f.location LIKE ? " +
                "ORDER BY f.created_at DESC";
        List<FoundItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%"; // 模糊匹配模式
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    /**
     * 按多个条件组合筛选招领记录
     * @param category 物品类别（可为空）
     * @param location 地点关键词（可为空）
     * @param status 状态（可为空）
     * @return 符合条件的招领列表
     * @throws SQLException 数据库操作异常
     */
    public List<FoundItem> findByFilter(String category, String location, String status) throws SQLException {
        // 动态构建 SQL，仅包含非空的条件参数
        StringBuilder sql = new StringBuilder("SELECT f.*, u.username FROM found_items f LEFT JOIN users u ON f.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            sql.append(" AND f.category = ?");
            params.add(category);
        }
        if (location != null && !location.isEmpty()) {
            sql.append(" AND f.location LIKE ?");
            params.add("%" + location + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND f.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY f.created_at DESC");

        List<FoundItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i)); // 动态设置参数
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    /**
     * 将 ResultSet 当前行映射为 FoundItem 对象
     * @param rs JDBC 结果集
     * @return 映射后的 FoundItem 对象
     * @throws SQLException 结果集读取异常
     */
    private FoundItem mapItem(ResultSet rs) throws SQLException {
        FoundItem item = new FoundItem();
        String idStr = rs.getString("id");
        if (idStr != null) item.setId(UUID.fromString(idStr)); // 字符串转 UUID
        String uidStr = rs.getString("user_id");
        if (uidStr != null) item.setUserId(UUID.fromString(uidStr));
        item.setItemName(rs.getString("item_name"));       // 物品名称
        item.setDescription(rs.getString("description"));  // 描述
        item.setCategory(rs.getString("category"));        // 类别
        item.setLocation(rs.getString("location"));          // 地点
        Timestamp ts = rs.getTimestamp("found_time");
        if (ts != null) item.setFoundTime(ts.toInstant()); // 拾取时间
        item.setImageUrl(rs.getString("image_url"));       // 图片URL
        item.setContact(rs.getString("contact"));            // 联系方式
        item.setStatus(rs.getString("status"));              // 状态
        ts = rs.getTimestamp("created_at");
        if (ts != null) item.setCreatedAt(ts.toInstant());   // 创建时间
        item.setUsername(rs.getString("username"));          // 发布者用户名（联表查询）
        return item;
    }
}
