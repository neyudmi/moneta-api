package com.example.expense_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.expense_service.entities.Limit;

@Repository
public interface LimitRepository extends JpaRepository<Limit, UUID> {

    List<Limit> findByUserId(UUID userId);

    Optional<Limit> findByIdAndUserId(UUID limitId, UUID userId);
}
