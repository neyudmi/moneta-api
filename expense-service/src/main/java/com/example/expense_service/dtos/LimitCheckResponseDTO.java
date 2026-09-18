package com.example.expense_service.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitCheckResponseDTO {

    private UUID limitId;
    private String limitName;
    private BigDecimal limitAmount;
    private BigDecimal totalSpent;
    private double usagePercent;
    private boolean warning;
    private String level;

}
