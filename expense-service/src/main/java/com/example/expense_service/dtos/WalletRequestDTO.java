package com.example.expense_service.dtos;

import java.math.BigDecimal;

import com.example.expense_service.entities.Wallet.WalletType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private WalletType type;

    @NotNull
    @PositiveOrZero
    private BigDecimal initialBalance;

    @Size(max = 100)
    private String description;

}
