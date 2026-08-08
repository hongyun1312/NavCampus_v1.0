package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("seats")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Long id;

        private String name; // e.g., "A-101", "B-205"

    // AVAILABLE, OCCUPIED, MAINTENANCE
        private String status = "AVAILABLE";

    // NORMAL, POWER, WINDOW, DUAL
    private String type = "NORMAL";

    private String section; // "A Area", "Quiet Zone"
}
