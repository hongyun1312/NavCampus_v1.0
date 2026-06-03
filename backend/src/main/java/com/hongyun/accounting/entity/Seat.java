package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "A-101", "B-205"

    // AVAILABLE, OCCUPIED, MAINTENANCE
    @Column(nullable = false)
    private String status = "AVAILABLE";

    // NORMAL, POWER, WINDOW, DUAL
    private String type = "NORMAL";

    private String section; // "A Area", "Quiet Zone"
}
