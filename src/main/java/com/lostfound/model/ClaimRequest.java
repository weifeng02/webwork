package com.lostfound.model;

import java.time.Instant;
import java.util.UUID;

/**
 * 认领申请实体类
 * 对应数据库表 claim_requests，用于存储用户提交的认领申请
 * 状态说明：pending（待审核）、approved（已通过）、rejected（已拒绝）
 */
public class ClaimRequest {
    // 申请记录唯一标识（UUID 主键）
    private UUID id;
    // 关联的失物ID（外键关联 lost_items 表）
    private UUID lostItemId;
    // 关联的招领ID（外键关联 found_items 表）
    private UUID foundItemId;
    // 申请人的用户ID（外键关联 users 表）
    private UUID userId;
    // 状态：pending（待审核）、approved（已通过）、rejected（已拒绝）
    private String status;
    // 申请说明/证明材料
    private String message;
    // 申请创建时间
    private Instant createdAt;

    // 非持久化字段：联表查询后附加的展示信息
    private String lostItemName;   // 失物名称
    private String foundItemName;  // 招领名称
    private String username;       // 申请人用户名

    public ClaimRequest() {}

    public ClaimRequest(UUID id, UUID lostItemId, UUID foundItemId, UUID userId,
                        String status, String message, Instant createdAt) {
        this.id = id;
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.userId = userId;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getLostItemId() { return lostItemId; }
    public void setLostItemId(UUID lostItemId) { this.lostItemId = lostItemId; }

    public UUID getFoundItemId() { return foundItemId; }
    public void setFoundItemId(UUID foundItemId) { this.foundItemId = foundItemId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getLostItemName() { return lostItemName; }
    public void setLostItemName(String lostItemName) { this.lostItemName = lostItemName; }

    public String getFoundItemName() { return foundItemName; }
    public void setFoundItemName(String foundItemName) { this.foundItemName = foundItemName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
