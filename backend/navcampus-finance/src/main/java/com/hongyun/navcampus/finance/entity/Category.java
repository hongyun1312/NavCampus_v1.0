package com.hongyun.navcampus.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 分类实体。
 * 支持收入/支出类型、图标类名与颜色。
 */
@Data
@TableName("categories")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private CategoryType type;
    private String icon;
    private String color;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public enum CategoryType {
        INCOME, EXPENSE
    }
}
