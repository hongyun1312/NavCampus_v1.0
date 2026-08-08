package com.hongyun.navcampus.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * 验证码实体。
 * 存储邮箱/手机号验证码、过期时间与验证状态。
 */
@Data
@TableName("verification_codes")
public class VerificationCode {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    private CodeType type;
    private String target;
    private String code;
    private LocalDateTime expiresAt;
    private boolean verified;

    public enum CodeType {
        EMAIL, PHONE
    }
}
