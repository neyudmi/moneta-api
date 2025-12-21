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

    @Transactional
    public void deleteTransaction(UUID transactionId, UUID userId) {

        Transactions tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction không tồn tại"));

        if (!tx.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa giao dịch này");
        }

        Wallet wallet = tx.getWallet();

        // Hoàn lại ảnh hưởng của giao dịch
        if (tx.getType() == TransactionType.EXPENSE) {
            wallet.setCurrentBalance(wallet.getCurrentBalance().add(tx.getAmount()));
        } else {
            wallet.setCurrentBalance(wallet.getCurrentBalance().subtract(tx.getAmount()));
        }

        walletRepository.save(wallet);
        transactionRepository.delete(tx);
    }

    @Transactional
    public Transactions updateTransaction(UUID transactionId, UUID userId, TransactionDTO dto) {

        Transactions oldTx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction không tồn tại"));

        if (!oldTx.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa giao dịch này");
        }

        Wallet wallet = oldTx.getWallet();

        if (oldTx.getType() == TransactionType.EXPENSE) {
            wallet.setCurrentBalance(wallet.getCurrentBalance().add(oldTx.getAmount())); // hoàn tiền chi
        } else {
            wallet.setCurrentBalance(wallet.getCurrentBalance().subtract(oldTx.getAmount())); // hoàn tiền thu
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Hạng mục không tồn tại"));
            oldTx.setCategory(category);
        }

        if (dto.getAmount() != null) {
            oldTx.setAmount(dto.getAmount());
        }

        if (dto.getType() != null) {
            oldTx.setType(TransactionType.valueOf(dto.getType()));
        }

        if (dto.getDescription() != null) {
            oldTx.setDescription(dto.getDescription());
        }

        if (dto.getTransactionDate() != null) {
            oldTx.setTransactionDate(dto.getTransactionDate());
        }

        BigDecimal newAmount = oldTx.getAmount();
        TransactionType newType = oldTx.getType();

        if (newType == TransactionType.EXPENSE) {
            wallet.setCurrentBalance(wallet.getCurrentBalance().subtract(newAmount)); // chi
        } else {
            wallet.setCurrentBalance(wallet.getCurrentBalance().add(newAmount)); // thu
        }

        if (wallet.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Số dư ví không thể âm sau khi cập nhật giao dịch");
        }

        walletRepository.save(wallet);
        return transactionRepository.save(oldTx);
    }

    public Transactions getTransactionById(UUID transactionId, UUID userId) {
        Transactions tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction không tồn tại"));

        if (!tx.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem giao dịch này");
        }

        return tx;
    }

}