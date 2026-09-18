package com.example.expense_service.dtos;

import java.util.UUID;

public record IconResponseDTO(
        UUID id,
        String name,
        String fileName) {
}