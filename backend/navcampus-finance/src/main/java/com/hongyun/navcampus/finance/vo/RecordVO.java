package com.hongyun.navcampus.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "收支记录")
public class RecordVO {
    private Long id;
    private BigDecimal amount;
    private String type;
    private LocalDateTime time;
    private Long categoryId;
    private Long accountId;
    private Long targetAccountId;
    private String remark;
    private String location;
    private Long userId;
    private LocalDateTime createdAt;
}
