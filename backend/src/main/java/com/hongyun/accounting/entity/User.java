package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 用户实体。
 * 实现 UserDetails 以便与 Spring Security 集成。
 * 支持邮箱、手机号、头像与主题色等个性化信息。
 */
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String phone;
    
    private String avatar; // For dashboard personalization
    private String themeColor; // For theme switching

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    
    private boolean isBlacklisted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) role = Role.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 防止 role 为 null 时报错
        if (role == null) {
            return Collections.singletonList((GrantedAuthority) () -> "ROLE_USER");
        }
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_" + role.name());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    
    public enum Role {
        USER, ADMIN
    }
    @Override
    public boolean isAccountNonLocked() { return !isBlacklisted; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
