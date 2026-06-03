package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(length = 1000)
    private String content;
    
    @Enumerated(EnumType.STRING)
    private NotifyType type; 
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // null for broadcast
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    private boolean isRead = false;
    
    public enum NotifyType {
        SYSTEM, MAINTENANCE, EMERGENCY, ADMIN, SITE
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public NotifyType getType() { return type; }
    public void setType(NotifyType type) { this.type = type; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
