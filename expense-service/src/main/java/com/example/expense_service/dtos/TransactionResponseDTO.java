package com.example.expense_service.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionResponseDTO {

    private UUID id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
    private String type;
    private UUID walletId;
    private UUID categoryId;
    private UUID userId;
    private LocalDateTime createdAt;

    public TransactionResponseDTO() {
    }
}
