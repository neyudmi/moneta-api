package com.example.expense_service.services;

import com.example.expense_service.dtos.CreateWalletDTO;
import com.example.expense_service.models.Wallet;
import com.example.expense_service.repositories.LimitRepository;
import com.example.expense_service.repositories.WalletRepository;

import jakarta.transaction.Transactional;

import com.example.expense_service.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletImplement implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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

    // @Override
    // public void deleteWallet(UUID userId, UUID walletId) {

    // Wallet wallet = walletRepository.findById(walletId)
    // .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

    // if (!wallet.getUserId().equals(userId)) {
    // throw new RuntimeException("Bạn không có quyền xóa ví này");
    // }

    // transactionRepository.deleteByWalletId(walletId);
    // limitRepository.deleteByWalletId(walletId);
    // walletRepository.delete(wallet);
    // }

    @Override
    @Transactional
    public void deleteWallet(UUID userId, UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        if (!wallet.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa ví này");
        }

        transactionRepository.deleteByWalletId(walletId);
        limitRepository.deleteByWalletId(walletId);
        walletRepository.delete(wallet);
    }

    @Override
    public Wallet updateWallet(UUID userId, UUID walletId, CreateWalletDTO dto) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        if (!wallet.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa ví này");
        }

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            wallet.setName(dto.getName());
        }

        if (dto.getType() != null) {
            wallet.setType(dto.getType());
        }

        if (dto.getDescription() != null) {
            wallet.setDescription(dto.getDescription());
        }

        if (dto.getInitialBalance() != null) {

            BigDecimal oldInitial = wallet.getInitialBalance();
            BigDecimal newInitial = dto.getInitialBalance();

            if (newInitial.compareTo(oldInitial) != 0) {

                BigDecimal diff = newInitial.subtract(oldInitial);

                wallet.setInitialBalance(newInitial);

                wallet.setCurrentBalance(wallet.getCurrentBalance().add(diff));

                if (wallet.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Số dư hiện tại không thể âm sau khi cập nhật số dư ban đầu");
                }
            }
        }

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getWalletById(UUID userId, UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        if (!wallet.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem ví này");
        }

        return wallet;
    }

}