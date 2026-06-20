package com.lostfound.model;

import java.time.Instant;
import java.util.UUID;

/**
 * 失物实体类
 * 对应数据库表 lost_items，用于存储用户发布的丢失物品信息
 * 状态说明：pending（待找回）、matched（已匹配）、found（已找回）
 */
public class LostItem {
    // 失物唯一标识（UUID 主键）
    private UUID id;
    // 发布者的用户ID（外键关联 users 表）
    private UUID userId;
    // 物品名称
    private String itemName;
    // 物品描述
    private String description;
    // 物品类别（如电子产品、证件、钱包等）
    private String category;
    // 丢失地点
    private String location;
    // 丢失时间
    private Instant lostTime;
    // 物品图片的 URL 路径
    private String imageUrl;
    // 状态：pending（待找回）、matched（已匹配）、found（已找回）
    private String status;
    // 记录创建时间
    private Instant createdAt;

    // 非持久化字段：发布者用户名，用于联表查询后展示
    private String username;

    public LostItem() {}

    public LostItem(UUID id, UUID userId, String itemName, String description, String category,
                    String location, Instant lostTime, String imageUrl, String status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.location = location;
        this.lostTime = lostTime;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Instant getLostTime() { return lostTime; }
    public void setLostTime(Instant lostTime) { this.lostTime = lostTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
