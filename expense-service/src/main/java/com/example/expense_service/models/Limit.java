package com.example.expense_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "expense_limit")
@Getter
@Setter
public class Limit {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 255)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate; // có thể null nếu "Không xác định"

    @Column(name = "category_group_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID categoryGroupId; // null = không chọn group cha

    @Column(name = "category_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID categoryId; // null = không chọn category con

    @Column(name = "wallet_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID walletId; // null = áp dụng cho tất cả ví

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false)
    private RepeatType repeatType = RepeatType.NONE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
