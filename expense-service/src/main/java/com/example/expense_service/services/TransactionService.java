package com.example.expense_service.services;

import com.example.expense_service.dtos.TransactionDTO;
import com.example.expense_service.models.*;
import com.example.expense_service.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRefRepository userRepository;

    @Transactional
    public Transactions createTransaction(TransactionDTO request, UUID userId) {

        // UserRef userRef = userRepository.findById(userId)
        // .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        if (!wallet.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên ví này");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Hạng mục không tồn tại"));

        TransactionType type = TransactionType.valueOf(request.getType());
        BigDecimal amount = request.getAmount();
        BigDecimal currentBalance = wallet.getCurrentBalance();

        if (type == TransactionType.EXPENSE) {
            // Chi tiêu -> Trừ tiền
            wallet.setCurrentBalance(currentBalance.subtract(amount));
        } else {
            // Thu nhập -> Cộng tiền
            wallet.setCurrentBalance(currentBalance.add(amount));
        }

        Transactions transaction = new Transactions();
        transaction.setAmount(amount);
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(type);

        transaction.setUserId(userId);
        transaction.setWallet(wallet);
        transaction.setCategory(category);

        walletRepository.save(wallet);
        return transactionRepository.save(transaction);
    }

    public List<Transactions> getTransactionsByUserId(UUID userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
    }
}