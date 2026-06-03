package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    // PENDING, CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED, MISSED
    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime checkInTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private boolean reminderSent = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
