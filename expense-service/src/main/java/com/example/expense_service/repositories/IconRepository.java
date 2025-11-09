package com.example.expense_service.repositories;

import com.example.expense_service.models.Icon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface IconRepository extends JpaRepository<Icon, UUID> {
    Optional<Icon> findByFileName(String fileName);
}
