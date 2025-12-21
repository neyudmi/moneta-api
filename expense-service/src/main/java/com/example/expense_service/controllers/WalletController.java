package com.example.expense_service.controllers;

import com.example.expense_service.dtos.CreateWalletDTO;
import com.example.expense_service.models.Wallet;
import com.example.expense_service.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expense/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/add")
    public ResponseEntity<Wallet> createWallet(
            @RequestBody CreateWalletDTO dto,
            @RequestHeader("X-User-Id") UUID userId) {

        Wallet newWallet = walletService.createWallet(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Wallet>> getWalletsByUserId(
            @RequestHeader("X-User-Id") UUID userId) {

        List<Wallet> wallets = walletService.getWalletsByUserId(userId);
        return ResponseEntity.ok(wallets);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWalletById(
            @PathVariable UUID walletId,
            @RequestHeader("X-User-Id") UUID userId) {

        Wallet wallet = walletService.getWalletById(userId, walletId);
        return ResponseEntity.ok(wallet);
    }

    @DeleteMapping("/delete/{walletId}")
    public ResponseEntity<?> deleteWallet(
            @PathVariable UUID walletId,
            @RequestHeader("X-User-Id") UUID userId) {

        walletService.deleteWallet(userId, walletId);
        return ResponseEntity.ok("Xóa ví thành công");
    }

    @PutMapping("/update/{walletId}")
    public ResponseEntity<Wallet> updateWallet(
            @PathVariable UUID walletId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody CreateWalletDTO dto) {

        Wallet updatedWallet = walletService.updateWallet(userId, walletId, dto);
        return ResponseEntity.ok(updatedWallet);
    }

}