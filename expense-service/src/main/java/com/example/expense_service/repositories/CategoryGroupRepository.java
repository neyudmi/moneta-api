package com.example.expense_service.repositories;

import com.example.expense_service.dtos.ParentCategoryDTO;
import com.example.expense_service.models.CategoryGroup;
import com.example.expense_service.models.Icon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, UUID> {
    Optional<CategoryGroup> findByName(String groupName);

    @Query("SELECT new com.example.expense_service.dtos.ParentCategoryDTO(cg.id, cg.name, i.fileName) " +
            "FROM CategoryGroup cg JOIN cg.icon i")
    List<ParentCategoryDTO> findAllParentCategories();

}
