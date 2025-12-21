package com.example.expense_service.controllers;

import com.example.expense_service.dtos.LimitCheckResponseDTO;
import com.example.expense_service.dtos.LimitDTO;
import com.example.expense_service.dtos.LimitResponseDTO;
import com.example.expense_service.dtos.TransactionResponseDTO;
import com.example.expense_service.models.Limit;
import com.example.expense_service.services.LimitService;

import org.hibernate.id.uuid.UuidGenerator;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expense/limits")
public class LimitController {

    private final LimitService limitService;

    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @PostMapping("/add")
    public ResponseEntity<LimitResponseDTO> createLimit(
            @RequestBody LimitDTO dto,
            @RequestHeader("X-User-Id") UUID userId) {

        LimitResponseDTO response = limitService.createLimit(dto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<LimitResponseDTO>> getLimits(
            @RequestHeader("X-User-Id") UUID userId) {

        List<LimitResponseDTO> limits = limitService.getLimits(userId);
        return ResponseEntity.ok(limits);
    }

    @GetMapping("/{limitId}")
    public ResponseEntity<LimitResponseDTO> getLimitById(
            @PathVariable UUID limitId,
            @RequestHeader("X-User-Id") UUID userId) {

        LimitResponseDTO limit = limitService.getLimitById(limitId, userId);
        return ResponseEntity.ok(limit);
    }

    @DeleteMapping("/delete/{limitId}")
    public ResponseEntity<?> deleteLimit(
            @PathVariable UUID limitId,
            @RequestHeader("X-User-Id") UUID userId) {
        limitService.deleteLimit(limitId, userId);
        System.out.println("DEBUG: Đã vào hàm delete với ID: " + limitId);
        return ResponseEntity.ok("Xóa hạn mức thành công");
    }

    @PostMapping("update/{limitId}")
    public ResponseEntity<LimitResponseDTO> updateLitmit(
            @PathVariable UUID limitId,
            @RequestBody LimitDTO dto,
            @RequestHeader("X-User-Id") UUID userId) {
        LimitResponseDTO updated = limitService.updateLimit(limitId, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/check")
    public ResponseEntity<LimitCheckResponseDTO> checkLimit(
            @RequestParam UUID limitId,
            @RequestHeader("X-User-Id") UUID userId) {

        LimitCheckResponseDTO result = limitService.checkLimit(limitId, userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/limits/{limitId}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsForLimit(
            @PathVariable UUID limitId,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(
                limitService.getTransactionsForLimit(limitId, userId));
    }

}
