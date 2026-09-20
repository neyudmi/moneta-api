package com.example.expense_service.services;

import java.util.List;
import java.util.UUID;

import com.example.expense_service.dtos.WalletRequestDTO;
import com.example.expense_service.dtos.WalletResponseDTO;
import com.example.expense_service.entities.Wallet;
import com.example.expense_service.exceptions.ResourceNotFoundException;
import com.example.expense_service.repositories.WalletRepository;

import jakarta.transaction.Transactional;

public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public List<WalletResponseDTO> getAllWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WalletResponseDTO getWalletById(UUID walletId, UUID userId) {
        return walletRepository.findByIdAndUserId(walletId, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
    }

    @Transactional
    public WalletResponseDTO createWallet(UUID userId, WalletRequestDTO walletRequestDTO) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setName(walletRequestDTO.getName());
        wallet.setType(walletRequestDTO.getType());
        wallet.setInitialBalance(walletRequestDTO.getInitialBalance());
        wallet.setDescription(walletRequestDTO.getDescription());

        Wallet savedWallet = walletRepository.save(wallet);
        return toResponse(savedWallet);
    }

    @Transactional
    public WalletResponseDTO updateWallet(UUID userId, UUID walletId, WalletRequestDTO walletRequestDTO) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));

        wallet.setName(walletRequestDTO.getName());
        wallet.setType(walletRequestDTO.getType());
        wallet.setInitialBalance(walletRequestDTO.getInitialBalance());
        wallet.setDescription(walletRequestDTO.getDescription());

        Wallet updatedWallet = walletRepository.save(wallet);
        return toResponse(updatedWallet);
    }

    @Transactional
    public void deleteWallet(UUID userId, UUID walletId) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        walletRepository.delete(wallet);
    }

    private WalletResponseDTO toResponse(Wallet wallet) {
        return new WalletResponseDTO(
                wallet.getId().toString(),
                wallet.getName(),
                wallet.getType(),
                wallet.getInitialBalance(),
                wallet.getDescription());
    }

}
