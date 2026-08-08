package com.hongyun.navcampus.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "分类信息")
public class CategoryVO {
    private Long id;
    private String name;
    private String type;
    private String icon;
    private String color;
    private Long userId;
    private LocalDateTime createdAt;
}
