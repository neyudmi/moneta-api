package com.example.expense_service.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ReportCategoryItemDTO {
    private UUID categoryId;
    private String categoryName;
    private BigDecimal total;
}
