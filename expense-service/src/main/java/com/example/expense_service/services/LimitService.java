package com.example.expense_service.services;

import com.example.expense_service.dtos.LimitDTO;
import com.example.expense_service.dtos.LimitResponseDTO;
import com.example.expense_service.models.Category;
import com.example.expense_service.models.Limit;
import com.example.expense_service.models.RepeatType;
import com.example.expense_service.models.Wallet;
import com.example.expense_service.repositories.CategoryRepository;
import com.example.expense_service.repositories.LimitRepository;
import com.example.expense_service.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LimitService {

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletRepository walletRepository;

    // ============================
    // CREATE LIMIT
    // ============================
    public LimitResponseDTO createLimit(LimitDTO request, UUID userId) {

        Wallet wallet = null;
        if (request.getWalletId() != null) {
            wallet = walletRepository.findById(request.getWalletId())
                    .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

            if (!wallet.getUserId().equals(userId)) {
                throw new RuntimeException("Bạn không có quyền thao tác trên ví này");
            }
        }

        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Hạng mục không tồn tại"));
        }

        if (request.getCategoryGroupId() != null) {

        }

        Limit limit = new Limit();
        limit.setAmount(request.getAmount());
        limit.setName(request.getName());
        limit.setStartDate(request.getStartDate());
        limit.setEndDate(request.getEndDate());
        limit.setCategoryGroupId(request.getCategoryGroupId());
        limit.setCategoryId(request.getCategoryId());
        limit.setWalletId(request.getWalletId());
        limit.setUserId(userId);

        if (request.getRepeat() != null) {
            limit.setRepeatType(RepeatType.valueOf(request.getRepeat()));
        } else {
            limit.setRepeatType(RepeatType.NONE);
        }

        limitRepository.save(limit);

        return toDTO(limit);
    }

    public List<LimitResponseDTO> getLimits(UUID userId) {
        return limitRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UUID> expandCategories(Limit limit) {

        UUID userId = limit.getUserId();

        // 1. Chọn category con
        if (limit.getCategoryId() != null) {
            return List.of(limit.getCategoryId());
        }

        // 2. Chọn nhóm cha → tất cả category thuộc group + đúng user đó
        if (limit.getCategoryGroupId() != null) {
            return categoryRepository.findByGroupIdAndUserId(
                    limit.getCategoryGroupId(),
                    userId)
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
        }

        // 3. Tất cả category (toàn bộ con)
        return categoryRepository.findByUserId(userId)
                .stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }

    // ============================
    // DTO MAPPER
    // ============================

    private LimitResponseDTO toDTO(Limit limit) {
        LimitResponseDTO dto = new LimitResponseDTO();
        dto.setId(limit.getId());
        dto.setAmount(limit.getAmount());
        dto.setName(limit.getName());
        dto.setStartDate(limit.getStartDate());
        dto.setEndDate(limit.getEndDate());
        dto.setCategoryGroupId(limit.getCategoryGroupId());
        dto.setCategoryId(limit.getCategoryId());
        dto.setWalletId(limit.getWalletId());
        dto.setUserId(limit.getUserId());
        dto.setCreatedAt(limit.getCreatedAt());
        dto.setRepeat(limit.getRepeatType().name());

        return dto;
    }
}
