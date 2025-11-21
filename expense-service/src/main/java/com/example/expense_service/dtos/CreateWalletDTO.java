package com.example.expense_service.dtos;

import com.example.expense_service.models.WalletType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateWalletDTO {
    private String name;
    private BigDecimal initialBalance;
    private WalletType type;
    private String description;
}