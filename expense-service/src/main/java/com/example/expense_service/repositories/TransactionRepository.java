package com.example.expense_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.expense_service.entities.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserId(UUID userId);

    Optional<Transaction> findByIdAndUserId(UUID transactionId, UUID userId);
}
