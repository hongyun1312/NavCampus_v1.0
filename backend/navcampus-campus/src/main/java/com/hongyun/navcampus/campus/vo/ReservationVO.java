package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "预约信息")
public class ReservationVO {
    @Schema(description = "预约ID")
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "座位ID")
    private Long seatId;
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "签到时间")
    private LocalDateTime checkInTime;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
