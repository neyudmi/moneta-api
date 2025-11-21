package com.example.expense_service.services;

import com.example.expense_service.dtos.CategoryGroupDto;
import com.example.expense_service.dtos.CreateCategoryDTO;
import com.example.expense_service.models.Category;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryGroupDto> getGroupedCategories(UUID userId);

    Category createCategory(CreateCategoryDTO dto);

    void deleteCategory(UUID categoryId, UUID userId);
}
