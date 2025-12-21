package com.example.expense_service.services;

import com.example.expense_service.dtos.CategoryDto;
import com.example.expense_service.dtos.CategoryGroupDto;
import com.example.expense_service.dtos.CreateCategoryDTO;
import com.example.expense_service.dtos.UpdateCategoryDTO;
import com.example.expense_service.models.Category;
import com.example.expense_service.models.CategoryGroup;
import com.example.expense_service.models.Icon;
import com.example.expense_service.repositories.CategoryGroupRepository;
import com.example.expense_service.repositories.CategoryRepository;
import com.example.expense_service.repositories.IconRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplement implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryGroupRepository groupRepository;
    private final IconRepository iconRepository;

    public CategoryServiceImplement(CategoryRepository categoryRepository,
            CategoryGroupRepository groupRepository,
            IconRepository iconRepository) {
        this.categoryRepository = categoryRepository;
        this.groupRepository = groupRepository;
        this.iconRepository = iconRepository;
    }

    @Override
    public List<CategoryGroupDto> getGroupedCategories(UUID userId) {
        List<CategoryGroup> groups = groupRepository.findAll();
        List<Category> categories = (userId == null)
                ? categoryRepository.findByUserId(null)
                : categoryRepository.findByUserId(userId);

        if (categories.isEmpty()) {
            categories = categoryRepository.findByUserId(null);
        }

        Map<UUID, List<Category>> grouped = categories.stream()
                .collect(Collectors.groupingBy(c -> c.getGroup().getId()));

        List<CategoryGroupDto> result = new ArrayList<>();
        for (CategoryGroup g : groups) {
            List<CategoryDto> cats = new ArrayList<>();
            if (grouped.containsKey(g.getId())) {
                cats = grouped.get(g.getId()).stream()
                        .map(c -> new CategoryDto(
                                c.getId(),
                                c.getName(),
                                c.getIcon().getFileName(),
                                c.getIcon().getId()))
                        .collect(Collectors.toList());
            }

            result.add(new CategoryGroupDto(
                    g.getId(),
                    g.getName(),
                    g.getIcon() != null ? g.getIcon().getFileName() : null,
                    cats));
        }

        return result;
    }

    public Category createCategory(CreateCategoryDTO dto) {
        Icon icon = iconRepository.findById(dto.getIconId())
                .orElseThrow(() -> new EntityNotFoundException("Icon not found: " + dto.getIconId()));

        CategoryGroup group = null;
        if (dto.getGroupId() != null) {
            group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("CategoryGroup not found: " + dto.getGroupId()));
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setUserId(dto.getUserId());
        category.setIcon(icon);

        category.setGroup(group);

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID categoryId, UUID userId) {
        categoryRepository.deleteByIdAndUserId(categoryId, userId);
    }

    @Override
    public Category updateCategory(UUID categoryId, UUID userId, UpdateCategoryDTO dto) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));

        if (category.getUserId() != null && !category.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa danh mục này");
        }

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            category.setName(dto.getName());
        }

        if (dto.getIconId() != null) {
            Icon icon = iconRepository.findById(dto.getIconId())
                    .orElseThrow(() -> new EntityNotFoundException("Icon not found: " + dto.getIconId()));
            category.setIcon(icon);
        }

        if (dto.getGroupId() != null) {
            CategoryGroup group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("CategoryGroup not found: " + dto.getGroupId()));
            category.setGroup(group);
        }

        return categoryRepository.save(category);
    }

}
