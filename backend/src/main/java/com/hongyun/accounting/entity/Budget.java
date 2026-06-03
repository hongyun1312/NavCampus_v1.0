package com.hongyun.accounting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 预算实体。
 * 支持整体预算与分类预算，按周期（yyyy-MM）设置额度。
 */
@Data
@Entity
@Table(name = "budgets")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetType type; // TOTAL, CATEGORY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // Null if type is TOTAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    // E.g. "2023-12" for monthly budget
    @Column(nullable = false)
    private String period; 

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum BudgetType {
        TOTAL, CATEGORY
    }
}
