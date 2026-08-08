package com.hongyun.navcampus.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统日志实体。
 * 记录用户操作行为，用于审计与安全追踪。
 */
@Data
@TableName("system_logs")
public class SystemLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String action;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    private User user;

    private String details;

    private String ipAddress;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
