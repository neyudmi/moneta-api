package com.example.expense_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.expense_service.entities.Transaction.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @Size(max = 100)
    private String description;

    @NotNull
    private LocalDate transactionDate;

    @NotNull
    private TransactionType type;

    @NotNull
    private UUID categoryId;

    @NotNull
    private UUID walletId;
}
