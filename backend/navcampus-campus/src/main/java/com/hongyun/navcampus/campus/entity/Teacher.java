package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@TableName("teachers")
public class Teacher {
    @TableId(type = IdType.AUTO)
    private Long id;

        private String name;

    private String title; // 职称
    private String department; // 学院/系
    private String email;
    private String phone;
    
        private String researchArea; // 研究方向
    
        private String bio; // 简介
}
