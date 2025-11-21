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
            }

            return r;
        }).toList();

        return ResponseEntity.ok(responseList);
    }

}