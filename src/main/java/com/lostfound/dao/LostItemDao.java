package com.lostfound.dao;

import com.lostfound.model.LostItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 失物数据访问对象（DAO）
 * 负责失物表（lost_items）的增删改查操作，使用 JDBC 连接 MySQL 数据库
 * 支持按条件筛选、全文搜索和模糊查询功能
 */
public class LostItemDao {

    /**
     * 插入一条新的失物记录
     * @param item 要插入的失物对象（包含物品名称、描述、类别、地点等）
     * @throws SQLException 数据库操作异常
     */
    public void insert(LostItem item) throws SQLException {
        String sql = "INSERT INTO lost_items (id, user_id, item_name, description, category, location, lost_time, image_url, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); // 获取数据库连接
             PreparedStatement ps = conn.prepareStatement(sql)) { // 预编译 SQL 防注入
            ps.setString(1, item.getId() != null ? item.getId().toString() : null); // UUID 转字符串
            ps.setString(2, item.getUserId() != null ? item.getUserId().toString() : null); // 发布者用户ID
            ps.setString(3, item.getItemName());   // 物品名称
            ps.setString(4, item.getDescription());  // 物品描述
            ps.setString(5, item.getCategory());     // 物品类别
            ps.setString(6, item.getLocation());     // 丢失地点
            ps.setTimestamp(7, item.getLostTime() != null ? Timestamp.from(item.getLostTime()) : null); // 丢失时间
            ps.setString(8, item.getImageUrl());     // 图片URL
            ps.setString(9, item.getStatus());       // 状态
            ps.setTimestamp(10, item.getCreatedAt() != null ? Timestamp.from(item.getCreatedAt()) : null); // 创建时间
            ps.executeUpdate(); // 执行插入操作
        }
    }

    /**
     * 根据 ID 查询失物详情
     * @param id 失物记录 UUID
     * @return 失物对象，未找到返回 null
     * @throws SQLException 数据库操作异常
     */
    public LostItem findById(UUID id) throws SQLException {
        // LEFT JOIN 联表查询用户名称，用于展示发布者信息
        String sql = "SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id WHERE l.id = ?";
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
     * 查询所有失物记录（按创建时间降序排列）
     * @return 失物列表
     * @throws SQLException 数据库操作异常
     */
    public List<LostItem> findAll() throws SQLException {
        String sql = "SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id ORDER BY l.created_at DESC";
        List<LostItem> list = new ArrayList<>();
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
     * 按状态查询失物记录
     * @param status 状态值（pending、matched、found）
     * @return 符合条件的失物列表
     * @throws SQLException 数据库操作异常
     */
    public List<LostItem> findByStatus(String status) throws SQLException {
        String sql = "SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id WHERE l.status = ? ORDER BY l.created_at DESC";
        List<LostItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); // 设置状态参数
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    /**
     * 按类别查询失物记录
     * @param category 物品类别（如电子产品、证件等）
     * @return 符合条件的失物列表
     * @throws SQLException 数据库操作异常
     */
    public List<LostItem> findByCategory(String category) throws SQLException {
        String sql = "SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id WHERE l.category = ? ORDER BY l.created_at DESC";
        List<LostItem> list = new ArrayList<>();
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
     * 按地点模糊查询失物记录
     * @param location 地点关键词（如图书馆、食堂等）
     * @return 地点包含关键词的失物列表
     * @throws SQLException 数据库操作异常
     */
    public List<LostItem> findByLocation(String location) throws SQLException {
        // MySQL 使用 LIKE 进行模糊匹配（% 表示任意字符）
        String sql = "SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id WHERE l.location LIKE ? ORDER BY l.created_at DESC";
        List<LostItem> list = new ArrayList<>();
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
     * 按用户 ID 查询该用户发布的所有失物
     * @param userId 用户 UUID
     * @return 该用户的失物列表
     * @throws SQLException 数据库操作异常
     */
    public List<LostItem> findByUserId(UUID userId) throws SQLException {
        String sql = "SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id WHERE l.user_id = ? ORDER BY l.created_at DESC";
        List<LostItem> list = new ArrayList<>();
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
     * 更新失物记录
     * @param item 包含更新后的失物信息
     * @throws SQLException 数据库操作异常
     */
    public void update(LostItem item) throws SQLException {
        String sql = "UPDATE lost_items SET item_name=?, description=?, category=?, location=?, lost_time=?, image_url=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setString(2, item.getDescription());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getLocation());
            ps.setTimestamp(5, item.getLostTime() != null ? Timestamp.from(item.getLostTime()) : null);
            ps.setString(6, item.getImageUrl());
            ps.setString(7, item.getStatus());
            ps.setString(8, item.getId().toString());
            ps.executeUpdate();
        }
    }

    /**
     * 根据 ID 删除失物记录
     * @param id 失物 UUID
     * @throws SQLException 数据库操作异常
     */
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM lost_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    /**
     * 全文关键词搜索失物记录
     * 在物品名称、描述、类别、地点中查找包含关键词的记录
     * @param keyword 搜索关键词
     * @return 匹配的失物列表
     * @throws SQLException 数据库操作异常
     */
    public List<LostItem> search(String keyword) throws SQLException {
        // MySQL 使用 LIKE 进行模糊匹配，匹配多个字段
        String sql = "SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id " +
                "WHERE l.item_name LIKE ? OR l.description LIKE ? OR l.category LIKE ? OR l.location LIKE ? " +
                "ORDER BY l.created_at DESC";
        List<LostItem> list = new ArrayList<>();
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
     * 按多个条件组合筛选失物记录
     * @param category 物品类别（可为空）
     * @param location 地点关键词（可为空）
     * @param status 状态（可为空）
     * @return 符合条件的失物列表
     * @throws SQLException 数据库操作异常
     */
    public List<LostItem> findByFilter(String category, String location, String status) throws SQLException {
        // 动态构建 SQL，仅包含非空的条件参数
        StringBuilder sql = new StringBuilder("SELECT l.*, u.username FROM lost_items l LEFT JOIN users u ON l.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            sql.append(" AND l.category = ?");
            params.add(category);
        }
        if (location != null && !location.isEmpty()) {
            sql.append(" AND l.location LIKE ?");
            params.add("%" + location + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND l.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY l.created_at DESC");

        List<LostItem> list = new ArrayList<>();
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
     * 将 ResultSet 当前行映射为 LostItem 对象
     * @param rs JDBC 结果集
     * @return 映射后的 LostItem 对象
     * @throws SQLException 结果集读取异常
     */
    private LostItem mapItem(ResultSet rs) throws SQLException {
        LostItem item = new LostItem();
        String idStr = rs.getString("id");
        if (idStr != null) item.setId(UUID.fromString(idStr)); // 字符串转 UUID
        String uidStr = rs.getString("user_id");
        if (uidStr != null) item.setUserId(UUID.fromString(uidStr));
        item.setItemName(rs.getString("item_name"));       // 物品名称
        item.setDescription(rs.getString("description"));  // 描述
        item.setCategory(rs.getString("category"));        // 类别
        item.setLocation(rs.getString("location"));          // 地点
        Timestamp ts = rs.getTimestamp("lost_time");
        if (ts != null) item.setLostTime(ts.toInstant());    // 丢失时间
        item.setImageUrl(rs.getString("image_url"));        // 图片URL
        item.setStatus(rs.getString("status"));              // 状态
        ts = rs.getTimestamp("created_at");
        if (ts != null) item.setCreatedAt(ts.toInstant());   // 创建时间
        item.setUsername(rs.getString("username"));          // 发布者用户名（联表查询）
        return item;
    }
}
