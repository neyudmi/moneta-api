package com.example.expense_service.dtos;

import java.util.UUID;

public record CategoryResponseDTO(
        UUID id,
        String name,
        UUID iconId,
        String iconName,
        String iconFileName,
        UUID parentId) {
}