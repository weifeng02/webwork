package com.lostfound.model;

import java.time.Instant;
import java.util.UUID;

/**
 * 招领实体类
 * 对应数据库表 found_items，用于存储用户发布的拾到物品信息
 * 状态说明：pending（待认领）、returned（已归还）
 */
public class FoundItem {
    // 招领记录唯一标识（UUID 主键）
    private UUID id;
    // 发布者的用户ID（外键关联 users 表）
    private UUID userId;
    // 物品名称
    private String itemName;
    // 物品描述
    private String description;
    // 物品类别（如电子产品、证件、钱包等）
    private String category;
    // 拾取地点
    private String location;
    // 拾取时间
    private Instant foundTime;
    // 物品图片的 URL 路径
    private String imageUrl;
    // 拾到者的联系方式（手机号或微信）
    private String contact;
    // 状态：pending（待认领）、returned（已归还）
    private String status;
    // 记录创建时间
    private Instant createdAt;

    // 非持久化字段：发布者用户名，用于联表查询后展示
    private String username;

    public FoundItem() {}

    public FoundItem(UUID id, UUID userId, String itemName, String description, String category,
                     String location, Instant foundTime, String imageUrl, String contact,
                     String status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.location = location;
        this.foundTime = foundTime;
        this.imageUrl = imageUrl;
        this.contact = contact;
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

    public Instant getFoundTime() { return foundTime; }
    public void setFoundTime(Instant foundTime) { this.foundTime = foundTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
