package com.hongyun.navcampus.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "预算信息")
public class BudgetVO {
    private Long id;
    private BigDecimal amount;
    private String type;
    private Long categoryId;
    private Long userId;
    private String period;
    private LocalDateTime createdAt;
}
