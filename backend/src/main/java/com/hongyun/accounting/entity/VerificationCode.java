package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * 验证码实体。
 * 存储邮箱/手机号验证码、过期时间与验证状态。
 */
@Data
@Entity
@Table(name = "verification_codes")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    @Enumerated(EnumType.STRING)
    private CodeType type;
    private String target;
    private String code;
    private LocalDateTime expiresAt;
    private boolean verified;

    public enum CodeType {
        EMAIL, PHONE
    }
}
