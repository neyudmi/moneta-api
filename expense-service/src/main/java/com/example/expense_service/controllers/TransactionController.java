package com.example.expense_service.controllers;

import com.example.expense_service.dtos.TransactionDTO;
import com.example.expense_service.dtos.TransactionResponseDTO;
import com.example.expense_service.models.Transactions;
import com.example.expense_service.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expense/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/add")
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody TransactionDTO dto,
            @RequestHeader("X-User-Id") UUID userId) {

        Transactions saved = transactionService.createTransaction(dto, userId);

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(saved.getId());
        response.setAmount(saved.getAmount());
        response.setDescription(saved.getDescription());
        response.setTransactionDate(saved.getTransactionDate());
        response.setType(saved.getType().name());
        response.setUserId(saved.getUserId());
        response.setCreatedAt(saved.getCreatedAt());

        if (saved.getWallet() != null) {
            response.setWalletId(saved.getWallet().getId());
        }

        if (saved.getCategory() != null) {
            response.setCategoryId(saved.getCategory().getId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByUserId(
            @RequestHeader("X-User-Id") UUID userId) {

        List<Transactions> transactions = transactionService.getTransactionsByUserId(userId);

        List<TransactionResponseDTO> responseList = transactions.stream().map(t -> {
            TransactionResponseDTO r = new TransactionResponseDTO();
            r.setId(t.getId());
            r.setAmount(t.getAmount());
            r.setDescription(t.getDescription());
            r.setTransactionDate(t.getTransactionDate());
            r.setType(t.getType().name());
            r.setUserId(t.getUserId());
            r.setCreatedAt(t.getCreatedAt());

            if (t.getWallet() != null) {
                r.setWalletId(t.getWallet().getId());
            }

            if (t.getCategory() != null) {
                r.setCategoryId(t.getCategory().getId());
                r.setName(t.getCategory().getName());
            }

            return r;
        }).toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @PathVariable UUID transactionId,
            @RequestHeader("X-User-Id") UUID userId) {

        Transactions transaction = transactionService.getTransactionById(transactionId, userId);

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setType(transaction.getType().name());
        response.setUserId(transaction.getUserId());
        response.setCreatedAt(transaction.getCreatedAt());

        if (transaction.getWallet() != null) {
            response.setWalletId(transaction.getWallet().getId());
        }
        if (transaction.getCategory() != null) {
            response.setCategoryId(transaction.getCategory().getId());
            response.setName(transaction.getCategory().getName());
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable UUID transactionId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody TransactionDTO dto) {

        Transactions updated = transactionService.updateTransaction(transactionId, userId, dto);

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(updated.getId());
        response.setAmount(updated.getAmount());
        response.setDescription(updated.getDescription());
        response.setTransactionDate(updated.getTransactionDate());
        response.setType(updated.getType().name());
        response.setUserId(updated.getUserId());
        response.setCreatedAt(updated.getCreatedAt());

        if (updated.getWallet() != null) {
            response.setWalletId(updated.getWallet().getId());
        }
        if (updated.getCategory() != null) {
            response.setCategoryId(updated.getCategory().getId());
            response.setName(updated.getCategory().getName());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{transactionId}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable UUID transactionId,
            @RequestHeader("X-User-Id") UUID userId) {

        transactionService.deleteTransaction(transactionId, userId);
        return ResponseEntity.ok("Xóa giao dịch thành công");
    }

}