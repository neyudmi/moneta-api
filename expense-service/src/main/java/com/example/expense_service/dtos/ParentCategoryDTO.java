package com.example.expense_service.dtos;

import java.util.UUID;

public class ParentCategoryDTO {
    private UUID id;
    private String name;
    private String iconFile;

    public ParentCategoryDTO(UUID id, String name, String iconFile) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconFile() {
        return iconFile;
    }
}