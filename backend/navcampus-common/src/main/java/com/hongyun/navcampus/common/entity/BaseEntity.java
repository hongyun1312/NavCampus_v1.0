package com.hongyun.navcampus.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体基类。
 * 包含审计字段：创建时间、更新时间、创建人、更新人、逻辑删除标志。
 * 所有业务实体继承此类，由 MetaObjectHandler 自动填充审计字段。
 */
@Data
public abstract class BaseEntity {

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableLogic
    @TableField(select = false)
    private Integer delFlag;
}