package com.hongyun.navcampus.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "反馈提交请求")
public class FeedbackRequest {
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    @Schema(description = "评分(1-5)")
    private Integer rate;

    @Schema(description = "反馈内容")
    private String content;
}
