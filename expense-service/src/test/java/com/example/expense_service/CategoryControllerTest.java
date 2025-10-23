package com.example.expense_service;


import com.example.expense_service.Controller.CategoryController;
import com.example.expense_service.DTO.CategoryDTO;
import com.example.expense_service.Service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private UUID categoryId;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    public void testGetAllByUserId() {
        CategoryDTO dto1 = new CategoryDTO("Food", "🍔");
        CategoryDTO dto2 = new CategoryDTO("Travel", "✈️");

        when(categoryService.findAllCategoryByUserId(userId)).thenReturn(Arrays.asList(dto1, dto2));

        List<CategoryDTO> result = categoryController.getAll(userId);

        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getTitle());
        verify(categoryService, times(1)).findAllCategoryByUserId(userId);
    }
    

    @Test
    public void testCreateCategory() {
        CategoryDTO request = new CategoryDTO("Shopping", "🛍️", userId);
        CategoryDTO saved = new CategoryDTO(categoryId, "Shopping", "🛍️", userId);

        when(categoryService.createCategory(request)).thenReturn(saved);

        CategoryDTO result = categoryController.create(request);

        assertNotNull(result);
        assertEquals("Shopping", result.getTitle());
        assertEquals("🛍️", result.getIconId());
        verify(categoryService, times(1)).createCategory(request);
    }

    @Test
    public void testUpdateCategory() {
        CategoryDTO update = new CategoryDTO("Entertainment", "🎮", userId);
        CategoryDTO updated = new CategoryDTO(categoryId, "Entertainment", "🎮", userId);

        when(categoryService.updateCategory(categoryId, update)).thenReturn(updated);

        CategoryDTO result = categoryController.update(categoryId, update);

        assertNotNull(result);
        assertEquals("Entertainment", result.getTitle());
        verify(categoryService, times(1)).updateCategory(categoryId, update);
    }

    @Test
    public void testDeleteCategory() {
        doNothing().when(categoryService).deleteCategory(categoryId);

        categoryController.delete(categoryId);

        verify(categoryService, times(1)).deleteCategory(categoryId);
    }
}

