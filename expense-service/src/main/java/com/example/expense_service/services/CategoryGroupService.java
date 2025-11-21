package com.example.expense_service.services;

import com.example.expense_service.dtos.ParentCategoryDTO;
import com.example.expense_service.repositories.CategoryGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    public List<ParentCategoryDTO> getAllParentCategories() {
        return categoryGroupRepository.findAllParentCategories();
    }
}