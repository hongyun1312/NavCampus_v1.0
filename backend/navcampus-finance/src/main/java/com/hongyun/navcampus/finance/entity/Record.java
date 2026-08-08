package com.hongyun.navcampus.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hongyun.navcampus.system.entity.User;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 收支记录实体。
 * 支持收入/支出/转账类型，精确到分钟的时间，分类与账户关联，富文本备注。
 */
@Data
@TableName("records")
public class Record {
    @TableId(type = IdType.AUTO)
    private Long id;

    private BigDecimal amount;
    private RecordType type;
    private LocalDateTime time;

    @TableField("category_id")
    private Long categoryId;

    @TableField(exist = false)
    private Category category;

    @TableField("account_id")
    private Long accountId;

    @TableField(exist = false)
    private Account account;

    @TableField("target_account_id")
    private Long targetAccountId;

    @TableField(exist = false)
    private Account targetAccount;

    private String remark;
    private String location;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public enum RecordType {
        INCOME, EXPENSE, TRANSFER
    }
}
