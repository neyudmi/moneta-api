package com.example.expense_service.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_service.dtos.WalletRequestDTO;
import com.example.expense_service.dtos.WalletResponseDTO;
import com.example.expense_service.services.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("expense/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<List<WalletResponseDTO>> getAllWallets(@AuthenticationPrincipal UUID userId) {
        List<WalletResponseDTO> wallets = walletService.getAllWallet(userId);
        return ResponseEntity.ok(wallets);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponseDTO> getWalletById(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID walletId) {
        WalletResponseDTO wallet = walletService.getWalletById(walletId, userId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody WalletRequestDTO walletRequest) {
        WalletResponseDTO wallet = walletService.createWallet(userId, walletRequest);
        return ResponseEntity.status(201).body(wallet);
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<WalletResponseDTO> updateWallet(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID walletId,
            @Valid @RequestBody WalletRequestDTO walletRequest) {
        WalletResponseDTO wallet = walletService.updateWallet(userId, walletId, walletRequest);
        return ResponseEntity.ok(wallet);
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Void> deleteWallet(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID walletId) {
        walletService.deleteWallet(userId, walletId);
        return ResponseEntity.noContent().build();
    }

}
