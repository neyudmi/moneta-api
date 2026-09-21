package com.example.expense_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.expense_service.entities.Icon;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IconRepository extends JpaRepository<Icon, UUID> {
    Optional<Icon> findByFileName(String fileName);
}
