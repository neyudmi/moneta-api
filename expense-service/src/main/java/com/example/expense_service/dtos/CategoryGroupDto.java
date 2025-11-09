package com.example.expense_service.dtos;

import java.util.List;
import java.util.UUID;

public class CategoryGroupDto {
    private UUID id;
    private String name;
    private String iconFile; // group.icon.fileName
    private List<CategoryDto> categories;

    public CategoryGroupDto(UUID id, String name, String iconFile, List<CategoryDto> categories) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
        this.categories = categories;
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

    public List<CategoryDto> getCategories() {
        return categories;
    }
}
