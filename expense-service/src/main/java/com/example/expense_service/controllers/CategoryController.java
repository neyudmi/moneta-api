package com.example.expense_service.controllers;

import com.example.expense_service.dtos.CategoryRequestDTO;
import com.example.expense_service.dtos.CategoryResponseDTO;
import com.example.expense_service.services.CategoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("expense/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(@AuthenticationPrincipal UUID userId) {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable UUID categoryId,
            @AuthenticationPrincipal UUID userId) {
        CategoryResponseDTO category = categoryService.getCategoryById(categoryId, userId);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CategoryRequestDTO categoryRequest) {
        CategoryResponseDTO category = categoryService.createCategory(userId, categoryRequest.getName(),
                categoryRequest.getIconId(),
                categoryRequest.getParentId());
        return ResponseEntity.status(201).body(category);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CategoryRequestDTO categoryRequest) {
        CategoryResponseDTO category = categoryService.updateCategory(
                categoryId,
                userId,
                categoryRequest.getName(),
                categoryRequest.getIconId(),
                categoryRequest.getParentId());
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal UUID userId) {
        categoryService.deleteCategory(categoryId, userId);
        return ResponseEntity.noContent().build();
    }

}
