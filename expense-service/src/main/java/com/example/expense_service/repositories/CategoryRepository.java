package com.example.expense_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.expense_service.entities.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // Count parent categories
    long countByUserIdIsNull();

    // Check if a category with the given parentId exists
    boolean existsByParentId(UUID parentId);

    // Find all parent categories
    List<Category> findByUserIdIsNull();

    // Find one parent category by category name
    Optional<Category> findByName(String name);

    // Check child category of a specific user exists
    boolean existsByUserId(UUID userId);

    // Find all child categories of a specific user
    List<Category> findByUserId(UUID userId);

    // Find one category of a specific user by categoryId
    Optional<Category> findByIdAndUserId(UUID categoryId, UUID userId);

}
