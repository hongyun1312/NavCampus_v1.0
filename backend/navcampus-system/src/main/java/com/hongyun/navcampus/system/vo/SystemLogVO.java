package com.hongyun.navcampus.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "系统日志")
public class SystemLogVO {
    @Schema(description = "日志ID")
    private Long id;
    @Schema(description = "操作类型")
    private String action;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "详情")
    private String details;
    @Schema(description = "IP地址")
    private String ipAddress;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
