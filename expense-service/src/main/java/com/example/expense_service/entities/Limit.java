package com.example.expense_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "limits", indexes = {
        @Index(name = "idx_limit_user", columnList = "user_id"),
        @Index(name = "idx_limit_category", columnList = "category_id"),
        @Index(name = "idx_limit_wallet", columnList = "wallet_id")
})
public class Limit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_limit_category"))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", foreignKey = @ForeignKey(name = "fk_limit_wallet"))
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false)
    private RepeatType repeatType = RepeatType.NONE;

    public enum RepeatType {
        NONE, // Không lặp
        DAILY, // Hàng ngày
        WEEKLY, // Hàng tuần
        MONTHLY, // Hàng tháng
        QUARTERLY, // Hàng quý
        YEARLY // Hàng năm
    }
}
