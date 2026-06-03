package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "courses")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name;
    private String room;
    private String teacher;

    @Column(nullable = false)
    private int weekday; // 1-7 (Mon-Sun)

    @Column(nullable = false)
    private LocalTime time;

    private boolean important;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
