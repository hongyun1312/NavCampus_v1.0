package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 反馈实体。
 * 用户对自习室等服务的评价反馈。
 */
@Data
@TableName("feedbacks")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    private User user;

    private Integer rate;
    private String content;
    private LocalDateTime createdAt;
}
