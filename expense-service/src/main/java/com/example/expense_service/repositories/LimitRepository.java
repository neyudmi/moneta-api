package com.example.expense_service.repositories;

import com.example.expense_service.models.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LimitRepository extends JpaRepository<Limit, UUID> {

    List<Limit> findAllByUserId(UUID userId);

    List<Limit> findAllByUserIdAndWalletId(UUID userId, UUID walletId);
}
