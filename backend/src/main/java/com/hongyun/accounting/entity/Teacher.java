package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "teachers")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String title; // 职称
    private String department; // 学院/系
    private String email;
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String researchArea; // 研究方向
    
    @Column(columnDefinition = "TEXT")
    private String bio; // 简介
}
