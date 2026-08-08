package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约实体。
 * 自习室座位预约记录，含时间区间、状态流转、签到提醒。
 */
@Data
@TableName("reservations")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    private User user;

    @TableField("seat_id")
    private Long seatId;

    @TableField(exist = false)
    private Seat seat;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status = "PENDING";

    private LocalDateTime checkInTime;

    @TableField("created_at")
    private LocalDateTime createdAt;

    private boolean reminderSent = false;
}
