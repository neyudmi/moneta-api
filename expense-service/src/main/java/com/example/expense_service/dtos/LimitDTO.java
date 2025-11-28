package com.example.expense_service.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LimitDTO {

    private BigDecimal amount;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate; // nullable nếu "Không xác định"

    private UUID categoryGroupId; // nullable = tất cả

    private UUID categoryId; // nullable = tất cả hoặc group

    private UUID walletId; // nullable = tất cả ví

    private String repeat;
}
