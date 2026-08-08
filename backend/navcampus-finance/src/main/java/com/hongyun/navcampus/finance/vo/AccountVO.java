package com.hongyun.navcampus.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "账户信息")
public class AccountVO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal balance;
    private Long userId;
    private String icon;
    private LocalDateTime createdAt;
}
