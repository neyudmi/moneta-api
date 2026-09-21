package com.example.expense_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.expense_service.entities.Transaction.TransactionType;

public record TransactionResponseDTO(
        UUID id,
        BigDecimal amount,
        String description,
        LocalDate transactionDate,
        TransactionType type,
        UUID categoryId,
        UUID walletId) {
}
