package com.example.expense_service.dtos;

import java.util.UUID;

public record ParentCategoryResponseDTO(
                UUID id,
                String name,
                IconResponseDTO icon) {
}