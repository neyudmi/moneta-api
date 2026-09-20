package com.example.expense_service.dtos;

import java.math.BigDecimal;

import com.example.expense_service.entities.Wallet.WalletType;

public record WalletResponseDTO(String id, String name, WalletType type, BigDecimal initialBalance,
        String description) {

}
