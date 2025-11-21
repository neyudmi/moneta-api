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
public class TransactionDTO {

    private BigDecimal amount;
    private UUID categoryId;
    private UUID walletId;
    private String description;
    private String type;
    private LocalDateTime transactionDate;
}