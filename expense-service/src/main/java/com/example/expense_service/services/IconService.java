package com.example.expense_service.services;

import com.example.expense_service.models.Icon;
import com.example.expense_service.repositories.IconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IconService {

    @Autowired
    private IconRepository iconRepository;

    public List<Icon> getAllIcons() {
        return iconRepository.findAll();
    }
}