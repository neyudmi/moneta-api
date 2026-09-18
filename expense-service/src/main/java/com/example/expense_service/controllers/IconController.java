package com.example.expense_service.controllers;

import com.example.expense_service.dtos.IconResponseDTO;
import com.example.expense_service.services.IconService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expense/icons")
public class IconController {
    private final IconService iconService;

    public IconController(IconService iconService) {
        this.iconService = iconService;
    }

    @GetMapping
    public ResponseEntity<List<IconResponseDTO>> getAllIcons() {
        List<IconResponseDTO> icons = iconService.getAllIcons();
        return ResponseEntity.ok(icons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IconResponseDTO> getIconById(@PathVariable UUID id) {
        return iconService.getIconById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}