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

    // Check categories child exists
    boolean existsByUserId(UUID userId);

    // Find child categories by userId
    List<Category> findByUserId(UUID userId);

    // Find parent categories (userId is null)
    List<Category> findByUserIdIsNull();

    // Find parent categories by category name
    Optional<Category> findByName(String name);

}
