package com.example.expense_service.services;

import com.example.expense_service.dtos.CreateWalletDTO;
import com.example.expense_service.models.Wallet;
import com.example.expense_service.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WalletImplement implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet createWallet(CreateWalletDTO dto, UUID userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setName(dto.getName());
        wallet.setType(dto.getType());
        wallet.setDescription(dto.getDescription());
        wallet.setInitialBalance(dto.getInitialBalance());
        wallet.setCurrentBalance(dto.getInitialBalance());

        return walletRepository.save(wallet);
    }

    @Override
    public List<Wallet> getWalletsByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }
}