package com.example.expense_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.expense_service.entities.Limit.RepeatType;

public record LimitResponseDTO(
        UUID id,
        BigDecimal amount,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        UUID walletId,
        UUID categoryId,
        RepeatType repeatType) {
}
