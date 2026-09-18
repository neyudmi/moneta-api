package com.example.expense_service.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
public class LimitResponseDTO {

    private UUID id;

    private BigDecimal amount;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private UUID categoryGroupId;

    private UUID categoryId;

    private UUID walletId;

    private UUID userId;

    private LocalDateTime createdAt;

    private String repeat;

}
