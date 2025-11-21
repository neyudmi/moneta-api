package com.example.expense_service.repositories;

import com.example.expense_service.models.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, UUID> {

    List<Transactions> findByUserIdOrderByTransactionDateDesc(UUID userId);
}