package com.example.expense_service.services;

import com.example.expense_service.dtos.IconResponseDTO;
import com.example.expense_service.repositories.IconRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IconService {
    private final IconRepository iconRepository;

    public IconService(IconRepository iconRepository) {
        this.iconRepository = iconRepository;
    }

    public List<IconResponseDTO> getAllIcons() {
        return iconRepository.findAll()
                .stream()
                .map(icon -> new IconResponseDTO(
                        icon.getId(),
                        icon.getFileName()))
                .toList();
    }

    public Optional<IconResponseDTO> getIconById(UUID id) {
        return iconRepository.findById(id)
                .map(icon -> new IconResponseDTO(
                        icon.getId(),
                        icon.getFileName()));
    }
}