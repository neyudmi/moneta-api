package com.example.expense_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.expense_service.entities.Limit.RepeatType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitRequestDTO {

    @NotNull
    @Positive
    private BigDecimal amount;

    @Size(max = 255)
    private String name;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private UUID walletId;

    private UUID categoryId;

    @NotNull
    private RepeatType repeatType;
}
