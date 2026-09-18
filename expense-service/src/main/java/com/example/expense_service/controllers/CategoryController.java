package com.example.expense_service.controllers;

import com.example.expense_service.dtos.CategoryResponseDTO;
import com.example.expense_service.services.CategoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

}
