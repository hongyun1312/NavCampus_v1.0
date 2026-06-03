package com.hongyun.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增记录请求 DTO。
 * 支持金额、类型、时间、分类、账户、备注等字段。
 */
@Data
public class RecordRequest {
    private BigDecimal amount;
    private String type;
    private String time;
    private Long categoryId;
    private Long accountId;
    private Long targetAccountId;
    private String remark;
    private String location;
}
