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

import com.example.expense_service.dtos.TransactionRequestDTO;
import com.example.expense_service.dtos.TransactionResponseDTO;
import com.example.expense_service.services.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("expense/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @AuthenticationPrincipal UUID userId) {
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID transactionId) {
        TransactionResponseDTO transaction = transactionService.getTransactionById(userId, transactionId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO transaction = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(201).body(transaction);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO transaction = transactionService.updateTransaction(userId, transactionId, request);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
