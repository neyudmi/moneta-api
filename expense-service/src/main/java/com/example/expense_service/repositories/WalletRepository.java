package com.example.expense_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.expense_service.entities.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    // Find all wallets of a specific user
    List<Wallet> findByUserId(UUID userId);

    // Find one wallet of a specific user by walletId
    Optional<Wallet> findByIdAndUserId(UUID walletId, UUID userId);

}
