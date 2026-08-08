package com.hongyun.navcampus.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "通知信息")
public class NotificationVO {
    @Schema(description = "通知ID")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "类型")
    private String type;
    @Schema(description = "目标用户ID")
    private Long userId;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @Schema(description = "是否已读")
    private boolean read;
}
