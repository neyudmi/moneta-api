package com.example.expense_service.services;

import com.example.expense_service.dtos.CategoryDto;
import com.example.expense_service.dtos.CategoryGroupDto;
import com.example.expense_service.models.Category;
import com.example.expense_service.models.CategoryGroup;
import com.example.expense_service.repositories.CategoryGroupRepository;
import com.example.expense_service.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplement implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryGroupRepository groupRepository;

    public CategoryServiceImplement(CategoryRepository categoryRepository, CategoryGroupRepository groupRepository) {
        this.categoryRepository = categoryRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<CategoryGroupDto> getGroupedCategories(UUID userId) {
        // Lấy tất cả group
        List<CategoryGroup> groups = groupRepository.findAll();

        // Lấy danh mục của user hoặc mặc định
        List<Category> categories = (userId == null)
                ? categoryRepository.findByUserId(null)
                : categoryRepository.findByUserId(userId);

        if (categories.isEmpty()) {
            // fallback nếu user chưa có category riêng
            categories = categoryRepository.findByUserId(null);
        }

        // Gom category theo group
        Map<UUID, List<Category>> grouped = categories.stream()
                .collect(Collectors.groupingBy(c -> c.getGroup().getId()));

        // Chuyển thành DTO
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
}
