package com.example.expense_service.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.expense_service.dtos.TransactionRequestDTO;
import com.example.expense_service.dtos.TransactionResponseDTO;
import com.example.expense_service.entities.Category;
import com.example.expense_service.entities.Transaction;
import com.example.expense_service.entities.Wallet;
import com.example.expense_service.exceptions.ResourceNotFoundException;
import com.example.expense_service.exceptions.UnauthorizedException;
import com.example.expense_service.repositories.CategoryRepository;
import com.example.expense_service.repositories.TransactionRepository;
import com.example.expense_service.repositories.WalletRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TransactionResponseDTO> getAllTransactions(UUID userId) {
        requireUser(userId);
        return transactionRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponseDTO getTransactionById(UUID userId, UUID transactionId) {
        requireUser(userId);
        Transaction transaction = findTransaction(userId, transactionId);
        return toResponse(transaction);
    }

    @Transactional
    public TransactionResponseDTO createTransaction(
            UUID userId,
            TransactionRequestDTO request) {
        requireUser(userId);
        Wallet wallet = findWallet(userId, request.getWalletId());
        Category category = findCategory(userId, request.getCategoryId());

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setCategory(category);
        transaction.setWallet(wallet);

        adjustBalance(wallet, transaction.getType(), transaction.getAmount());

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(
            UUID userId,
            UUID transactionId,
            TransactionRequestDTO request) {

        requireUser(userId);
        Transaction transaction = findTransaction(userId, transactionId);
        Wallet oldWallet = transaction.getWallet();

        adjustBalance(oldWallet, transaction.getType(), transaction.getAmount().negate());

        Wallet newWallet = findWallet(userId, request.getWalletId());
        Category category = findCategory(userId, request.getCategoryId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setCategory(category);
        transaction.setWallet(newWallet);

        adjustBalance(newWallet, transaction.getType(), transaction.getAmount());

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(UUID userId, UUID transactionId) {
        requireUser(userId);
        Transaction transaction = findTransaction(userId, transactionId);
        adjustBalance(
                transaction.getWallet(),
                transaction.getType(),
                transaction.getAmount().negate());
        transactionRepository.delete(transaction);
    }

    private Transaction findTransaction(UUID userId, UUID transactionId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + transactionId));
    }

    private Wallet findWallet(UUID userId, UUID walletId) {
        return walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found with id: " + walletId));
    }

    private Category findCategory(UUID userId, UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(category -> category.getUserId() == null
                        || category.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + categoryId));
    }

    private void adjustBalance(
            Wallet wallet,
            Transaction.TransactionType type,
            BigDecimal amount) {
        BigDecimal balanceChange = type == Transaction.TransactionType.INCOME
                ? amount
                : amount.negate();
        BigDecimal newBalance = wallet.getInitialBalance().add(balanceChange);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance in wallet");
        }
        wallet.setInitialBalance(newBalance);
        walletRepository.save(wallet);
    }

    private TransactionResponseDTO toResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getType(),
                transaction.getCategory().getId(),
                transaction.getWallet().getId());
    }

    private void requireUser(UUID userId) {
        if (userId == null) {
            throw new UnauthorizedException("Authenticated user is required");
        }
    }
}
