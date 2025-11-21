package com.example.expense_service.controllers;

import com.example.expense_service.dtos.CategoryGroupDto;
import com.example.expense_service.dtos.ParentCategoryDTO;
import com.example.expense_service.dtos.CreateCategoryDTO;
import com.example.expense_service.services.CategoryService;
import com.example.expense_service.services.CategoryGroupService;
import com.example.expense_service.models.Category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("expense/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryGroupService categoryGroupService;

    public CategoryController(CategoryService categoryService, CategoryGroupService categoryGroupService) {
        this.categoryService = categoryService;
        this.categoryGroupService = categoryGroupService;
    }

    @GetMapping("/grouped")
    public List<CategoryGroupDto> getGroupedCategories(@RequestParam(required = false) UUID userId) {
        return categoryService.getGroupedCategories(userId);
    }

    @GetMapping("/parents")
    public ResponseEntity<List<ParentCategoryDTO>> getAllParentCategories() {
        List<ParentCategoryDTO> categories = categoryGroupService.getAllParentCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/add")
    public ResponseEntity<Category> createCategory(@RequestBody CreateCategoryDTO createCategoryDTO) {
        Category newCategory = categoryService.createCategory(createCategoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("categoryId") UUID categoryId,
            @RequestHeader("X-User-Id") UUID userId) {
        categoryService.deleteCategory(categoryId, userId);
        return ResponseEntity.noContent().build();
    }
}
