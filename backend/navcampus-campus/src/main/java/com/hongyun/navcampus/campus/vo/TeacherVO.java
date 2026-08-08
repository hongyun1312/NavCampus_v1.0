package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "教师信息")
public class TeacherVO {
    private Long id;
    private String name;
    private String title;
    private String department;
    private String email;
    private String phone;
    private String researchArea;
    private String bio;
}
