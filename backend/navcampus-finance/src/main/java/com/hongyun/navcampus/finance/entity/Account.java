package com.hongyun.navcampus.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 账户实体。
 * 支持余额、账户类型（现金/银行卡/微信/支付宝）、图标与归属用户。
 */
@Data
@TableName("accounts")
public class Account {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private AccountType type;
    private BigDecimal balance;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    private String icon;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public enum AccountType {
        CASH, BANK_CARD, WECHAT, ALIPAY, OTHER
    }
}
