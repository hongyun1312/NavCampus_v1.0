package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "反馈信息")
public class FeedbackVO {
    private Long id;
    private Long userId;
    private Integer rate;
    private String content;
    private LocalDateTime createdAt;
}
