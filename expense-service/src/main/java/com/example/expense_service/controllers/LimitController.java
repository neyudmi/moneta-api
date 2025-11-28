package com.example.expense_service.controllers;

import com.example.expense_service.dtos.LimitDTO;
import com.example.expense_service.dtos.LimitResponseDTO;
import com.example.expense_service.models.Limit;
import com.example.expense_service.services.LimitService;
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

    // ===========================
    // CREATE LIMIT
    // ===========================
    @PostMapping("/add")
    public ResponseEntity<LimitResponseDTO> createLimit(
            @RequestBody LimitDTO dto,
            @RequestHeader("X-User-Id") UUID userId) {

        LimitResponseDTO response = limitService.createLimit(dto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===========================
    // GET LIMITS OF USER
    // ===========================
    @GetMapping("/all")
    public ResponseEntity<List<LimitResponseDTO>> getLimits(
            @RequestHeader("X-User-Id") UUID userId) {

        List<LimitResponseDTO> limits = limitService.getLimits(userId);
        return ResponseEntity.ok(limits);
    }
}
