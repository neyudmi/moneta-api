package com.example.expense_service.controllers;

import com.example.expense_service.models.Icon; // ⭐️ Đổi thành đường dẫn Entity Icon của bạn
import com.example.expense_service.services.IconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/expense/icons")
public class IconController {

    @Autowired
    private IconService iconService;

    @GetMapping
    public ResponseEntity<List<Icon>> getAllIcons() {
        List<Icon> icons = iconService.getAllIcons();
        return ResponseEntity.ok(icons);
    }
}