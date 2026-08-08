package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "课程信息")
public class CourseVO {
    private Long id;
    private Long userId;
    private String name;
    private String room;
    private String teacher;
    private int weekday;
    private LocalTime time;
    private boolean important;
    private LocalDateTime createdAt;
}
