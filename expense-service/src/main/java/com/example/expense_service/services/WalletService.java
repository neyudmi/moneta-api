package com.example.expense_service.services;

import com.example.expense_service.dtos.CreateWalletDTO;
import com.example.expense_service.models.Wallet;
import java.util.List;
import java.util.UUID;

public interface WalletService {
    Wallet createWallet(CreateWalletDTO dto, UUID userId);

    List<Wallet> getWalletsByUserId(UUID userId);

    void deleteWallet(UUID userId, UUID walletId);

    Wallet updateWallet(UUID userId, UUID walletId, CreateWalletDTO dto);

    Wallet getWalletById(UUID userId, UUID walletId);

}