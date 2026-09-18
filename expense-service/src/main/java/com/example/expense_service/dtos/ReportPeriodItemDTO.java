package com.example.expense_service.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReportPeriodItemDTO {
    private String period; // e.g. "2025-12-23" or "2025-Q4" or "2025-12"
    private BigDecimal total;
}
