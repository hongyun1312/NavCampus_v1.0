package com.hongyun.navcampus.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "座位可用性查询请求")
public class AvailabilityRequest {
    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private String startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private String endTime;
}
