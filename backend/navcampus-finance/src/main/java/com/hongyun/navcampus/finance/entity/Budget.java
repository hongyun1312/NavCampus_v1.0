package com.hongyun.navcampus.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 预算实体。
 * 支持整体预算与分类预算，按周期（yyyy-MM）设置额度。
 */
@Data
@TableName("budgets")
public class Budget {
    @TableId(type = IdType.AUTO)
    private Long id;

    private BigDecimal amount;
    private BudgetType type;

    @TableField("category_id")
    private Long categoryId;

    @TableField(exist = false)
    private Category category;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    private String period;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public enum BudgetType {
        TOTAL, CATEGORY
    }
}
