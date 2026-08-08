package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 课表实体。
 * 记录用户每周课程安排，含教室、教师、时间等信息。
 */
@Data
@TableName("courses")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    private String name;
    private String room;
    private String teacher;
    private int weekday;
    private LocalTime time;
    private boolean important;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
