package com.example.expense_service.dtos;

import java.util.UUID;

public class CategoryDto {
    private UUID id;
    private String name;
    private String iconFile; // icon.fileName
    private UUID iconId;

    public CategoryDto(UUID id, String name, String iconFile, UUID iconId) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
        this.iconId = iconId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconFile() {
        return iconFile;
    }

    public UUID getIconId() {
        return iconId;
    }
}
