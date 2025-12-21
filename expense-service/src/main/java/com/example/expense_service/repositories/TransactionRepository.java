package com.example.expense_service.repositories;

import com.example.expense_service.models.Transactions;
import com.example.expense_service.models.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, UUID> {

  List<Transactions> findByUserIdOrderByTransactionDateDesc(UUID userId);

  void deleteByWalletId(UUID walletId);

  // ======================
  // SUM EXPENSE FOR LIMIT
  // ======================
  @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transactions t
          WHERE t.userId = :userId
            AND t.wallet.id = :walletId
            AND t.category.id = :categoryId
            AND t.type = com.example.expense_service.models.TransactionType.EXPENSE
            AND t.transactionDate BETWEEN :start AND :end
      """)
  BigDecimal sumExpenseForLimit(
      @Param("userId") UUID userId,
      @Param("walletId") UUID walletId,
      @Param("categoryId") UUID categoryId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // ======================
  // GET TRANSACTIONS FOR LIMIT
  // ======================
  @Query("""
          SELECT t
          FROM Transactions t
          WHERE t.userId = :userId
            AND t.wallet.id = :walletId
            AND t.category.id = :categoryId
            AND t.type = com.example.expense_service.models.TransactionType.EXPENSE
            AND t.transactionDate BETWEEN :start AND :end
          ORDER BY t.transactionDate DESC
      """)
  List<Transactions> findTransactionsForLimit(
      @Param("userId") UUID userId,
      @Param("walletId") UUID walletId,
      @Param("categoryId") UUID categoryId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);
}
