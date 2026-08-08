package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "考试信息")
public class ExamVO {
    private Long id;
    private Long userId;
    private String name;
    private LocalDate date;
    private boolean important;
    private LocalDateTime createdAt;
}
