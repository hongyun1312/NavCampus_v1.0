package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "座位信息")
public class SeatVO {
    @Schema(description = "座位ID")
    private Long id;
    @Schema(description = "座位名称")
    private String name;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "类型")
    private String type;
    @Schema(description = "区域")
    private String section;
}
