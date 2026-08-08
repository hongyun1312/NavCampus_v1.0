package com.hongyun.navcampus.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import java.time.LocalDateTime;

/**
 * 通知实体。
 * 支持系统/维护/紧急/管理员/站内通知类型，可广播或定向发送。
 */
@TableName("notifications")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private NotifyType type;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    private User user;

    @TableField("created_at")
    private LocalDateTime createdAt;

    private boolean isRead = false;

    public enum NotifyType {
        SYSTEM, MAINTENANCE, EMERGENCY, ADMIN, SITE
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public NotifyType getType() { return type; }
    public void setType(NotifyType type) { this.type = type; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
