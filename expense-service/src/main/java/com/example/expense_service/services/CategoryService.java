package com.example.expense_service.services;

import com.example.expense_service.dtos.CategoryGroupDto;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryGroupDto> getGroupedCategories(UUID userId);
}
