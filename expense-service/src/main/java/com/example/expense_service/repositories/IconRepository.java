package com.example.expense_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.expense_service.entities.Icon;

import java.util.Optional;
import java.util.UUID;

public interface IconRepository extends JpaRepository<Icon, UUID> {
    Optional<Icon> findByFileName(String fileName);
}
