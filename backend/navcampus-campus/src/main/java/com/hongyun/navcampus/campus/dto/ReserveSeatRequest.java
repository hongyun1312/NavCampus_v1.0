package com.hongyun.navcampus.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "座位预约请求")
public class ReserveSeatRequest {
    @NotNull(message = "座位ID不能为空")
    @Schema(description = "座位ID")
    private Long seatId;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private String startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private String endTime;
}
