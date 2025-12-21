package com.example.expense_service.repositories;

import com.example.expense_service.models.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LimitRepository extends JpaRepository<Limit, UUID> {

    List<Limit> findAllByUserId(UUID userId);

    List<Limit> findAllByUserIdAndWalletId(UUID userId, UUID walletId);

    void deleteByWalletId(UUID walletId);

    Optional<Limit> findByUserIdAndWalletIdAndCategoryIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID userId,
            UUID walletId,
            UUID categoryId,
            LocalDateTime now1,
            LocalDateTime now2);
}
