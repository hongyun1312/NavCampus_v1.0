package com.hongyun.navcampus.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "新增记录请求")
public class RecordRequest {
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    @Schema(description = "金额")
    private BigDecimal amount;

    @NotNull(message = "类型不能为空")
    @Schema(description = "类型: INCOME/EXPENSE/TRANSFER")
    private String type;

    @NotNull(message = "时间不能为空")
    @Schema(description = "时间")
    private String time;

    @Schema(description = "分类ID")
    private Long categoryId;

    @NotNull(message = "账户ID不能为空")
    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "目标账户ID(转账)")
    private Long targetAccountId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "地点")
    private String location;
}
