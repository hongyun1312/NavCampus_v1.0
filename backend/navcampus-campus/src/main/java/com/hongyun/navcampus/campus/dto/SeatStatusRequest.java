package com.hongyun.navcampus.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "座位状态更新请求")
public class SeatStatusRequest {
    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态: AVAILABLE/OCCUPIED/MAINTENANCE")
    private String status;
}
