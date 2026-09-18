package com.example.expense_service.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ReportWalletItemDTO {
    private UUID walletId;
    private String walletName;
    private BigDecimal total;
}
