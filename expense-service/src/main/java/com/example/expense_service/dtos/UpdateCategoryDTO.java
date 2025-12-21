package com.example.expense_service.dtos;

import java.util.UUID;

import lombok.Data;

@Data
public class UpdateCategoryDTO {
    private String name;
    private UUID iconId;
    private UUID groupId;
}
