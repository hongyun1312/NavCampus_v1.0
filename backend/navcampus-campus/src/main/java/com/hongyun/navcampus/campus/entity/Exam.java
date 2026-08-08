package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考试实体。
 * 记录用户考试安排，含日期、重要性标记。
 */
@Data
@TableName("exams")
public class Exam {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    private String name;
    private LocalDate date;
    private boolean important;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
