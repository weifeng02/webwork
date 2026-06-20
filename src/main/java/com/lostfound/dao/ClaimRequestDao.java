package com.lostfound.dao;

import com.lostfound.model.ClaimRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 认领申请数据访问对象（DAO）
 * 负责认领申请表（claim_requests）的增删改查操作，使用 JDBC 连接 MySQL 数据库
 * 支持多表联查（LEFT JOIN）获取失物名称、招领名称和申请人信息
 */
public class ClaimRequestDao {

    /**
     * 插入一条新的认领申请记录
     * @param request 要插入的认领申请对象（包含失物ID、招领ID、用户ID、申请说明等）
     * @throws SQLException 数据库操作异常
     */
    public void insert(ClaimRequest request) throws SQLException {
        String sql = "INSERT INTO claim_requests (id, lost_item_id, found_item_id, user_id, status, message, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); // 获取数据库连接
             PreparedStatement ps = conn.prepareStatement(sql)) { // 预编译 SQL 防注入
            ps.setString(1, request.getId() != null ? request.getId().toString() : null); // UUID 转字符串
            ps.setString(2, request.getLostItemId() != null ? request.getLostItemId().toString() : null); // 关联失物ID
            ps.setString(3, request.getFoundItemId() != null ? request.getFoundItemId().toString() : null); // 关联招领ID
            ps.setString(4, request.getUserId() != null ? request.getUserId().toString() : null); // 申请人用户ID
            ps.setString(5, request.getStatus()); // 申请状态
            ps.setString(6, request.getMessage()); // 申请说明/证明材料
            ps.setTimestamp(7, request.getCreatedAt() != null ? Timestamp.from(request.getCreatedAt()) : null); // 创建时间
            ps.executeUpdate(); // 执行插入操作
        }
    }

    /**
     * 根据 ID 查询认领申请详情
     * @param id 申请记录 UUID
     * @return 认领申请对象，未找到返回 null
     * @throws SQLException 数据库操作异常
     */
    public ClaimRequest findById(UUID id) throws SQLException {
        // 多表 LEFT JOIN：关联失物表、招领表、用户表，获取名称和用户信息
        String sql = "SELECT cr.*, li.item_name as lost_item_name, fi.item_name as found_item_name, u.username " +
                "FROM claim_requests cr " +
                "LEFT JOIN lost_items li ON cr.lost_item_id = li.id " +
                "LEFT JOIN found_items fi ON cr.found_item_id = fi.id " +
                "LEFT JOIN users u ON cr.user_id = u.id " +
                "WHERE cr.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString()); // UUID 转字符串查询
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRequest(rs); // 映射结果集为对象
                }
            }
        }
        return null;
    }

    /**
     * 查询所有认领申请记录（按创建时间降序排列）
     * @return 认领申请列表
     * @throws SQLException 数据库操作异常
     */
    public List<ClaimRequest> findAll() throws SQLException {
        // 多表联查，获取完整的关联信息用于展示
        String sql = "SELECT cr.*, li.item_name as lost_item_name, fi.item_name as found_item_name, u.username " +
                "FROM claim_requests cr " +
                "LEFT JOIN lost_items li ON cr.lost_item_id = li.id " +
                "LEFT JOIN found_items fi ON cr.found_item_id = fi.id " +
                "LEFT JOIN users u ON cr.user_id = u.id " +
                "ORDER BY cr.created_at DESC";
        List<ClaimRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { // 遍历结果集
                list.add(mapRequest(rs));
            }
        }
        return list;
    }

    /**
     * 按用户 ID 查询该用户的所有认领申请
     * @param userId 用户 UUID
     * @return 该用户的认领申请列表
     * @throws SQLException 数据库操作异常
     */
    public List<ClaimRequest> findByUserId(UUID userId) throws SQLException {
        // 多表联查，筛选特定用户的申请
        String sql = "SELECT cr.*, li.item_name as lost_item_name, fi.item_name as found_item_name, u.username " +
                "FROM claim_requests cr " +
                "LEFT JOIN lost_items li ON cr.lost_item_id = li.id " +
                "LEFT JOIN found_items fi ON cr.found_item_id = fi.id " +
                "LEFT JOIN users u ON cr.user_id = u.id " +
                "WHERE cr.user_id = ? ORDER BY cr.created_at DESC";
        List<ClaimRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId.toString()); // UUID 转字符串
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRequest(rs));
                }
            }
        }
        return list;
    }

    /**
     * 按状态查询认领申请记录
     * @param status 状态值（pending、approved、rejected）
     * @return 符合条件的认领申请列表
     * @throws SQLException 数据库操作异常
     */
    public List<ClaimRequest> findByStatus(String status) throws SQLException {
        String sql = "SELECT cr.*, li.item_name as lost_item_name, fi.item_name as found_item_name, u.username " +
                "FROM claim_requests cr " +
                "LEFT JOIN lost_items li ON cr.lost_item_id = li.id " +
                "LEFT JOIN found_items fi ON cr.found_item_id = fi.id " +
                "LEFT JOIN users u ON cr.user_id = u.id " +
                "WHERE cr.status = ? ORDER BY cr.created_at DESC";
        List<ClaimRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); // 设置状态参数
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRequest(rs));
                }
            }
        }
        return list;
    }

    /**
     * 更新认领申请的状态（用于管理员审核通过/拒绝）
     * @param id 申请记录 UUID
     * @param status 新状态（approved 或 rejected）
     * @throws SQLException 数据库操作异常
     */
    public void updateStatus(UUID id, String status) throws SQLException {
        String sql = "UPDATE claim_requests SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); // 新状态
            ps.setString(2, id.toString()); // 记录 ID
            ps.executeUpdate();
        }
    }

    /**
     * 更新完整的认领申请记录
     * @param request 包含更新后的认领申请信息
     * @throws SQLException 数据库操作异常
     */
    public void update(ClaimRequest request) throws SQLException {
        String sql = "UPDATE claim_requests SET lost_item_id=?, found_item_id=?, user_id=?, status=?, message=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getLostItemId() != null ? request.getLostItemId().toString() : null);
            ps.setString(2, request.getFoundItemId() != null ? request.getFoundItemId().toString() : null);
            ps.setString(3, request.getUserId() != null ? request.getUserId().toString() : null);
            ps.setString(4, request.getStatus());
            ps.setString(5, request.getMessage());
            ps.setString(6, request.getId().toString());
            ps.executeUpdate();
        }
    }

    /**
     * 根据 ID 删除认领申请记录
     * @param id 申请记录 UUID
     * @throws SQLException 数据库操作异常
     */
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM claim_requests WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString()); // UUID 转字符串
            ps.executeUpdate();
        }
    }

    /**
     * 将 ResultSet 当前行映射为 ClaimRequest 对象
     * @param rs JDBC 结果集
     * @return 映射后的 ClaimRequest 对象
     * @throws SQLException 结果集读取异常
     */
    private ClaimRequest mapRequest(ResultSet rs) throws SQLException {
        ClaimRequest req = new ClaimRequest();
        // 读取字符串并转为 UUID
        String idStr = rs.getString("id");
        if (idStr != null) req.setId(UUID.fromString(idStr));
        String liId = rs.getString("lost_item_id");
        if (liId != null) req.setLostItemId(UUID.fromString(liId));
        String fiId = rs.getString("found_item_id");
        if (fiId != null) req.setFoundItemId(UUID.fromString(fiId));
        String uid = rs.getString("user_id");
        if (uid != null) req.setUserId(UUID.fromString(uid));
        req.setStatus(rs.getString("status")); // 状态
        req.setMessage(rs.getString("message")); // 申请说明
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) req.setCreatedAt(ts.toInstant()); // 创建时间
        // 联表查询的附加字段
        req.setLostItemName(rs.getString("lost_item_name")); // 失物名称
        req.setFoundItemName(rs.getString("found_item_name")); // 招领名称
        req.setUsername(rs.getString("username")); // 申请人用户名
        return req;
    }
}
