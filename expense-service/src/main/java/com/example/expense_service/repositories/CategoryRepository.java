package com.example.expense_service.repositories;

import com.example.expense_service.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);

    List<Category> findByUserIdOrUserIdIsNull(UUID userId);

    List<Category> findByGroupIdAndUserId(UUID groupId, UUID userId);

    Optional<Category> findByNameAndUserId(String name, UUID userId);

    @Transactional
    void deleteByIdAndUserId(UUID categoryId, UUID userId);
}
