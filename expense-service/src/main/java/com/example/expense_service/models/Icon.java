package com.example.expense_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "icons")
public class Icon {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "file_name", nullable = false, length = 100)
    private String fileName;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Icon() {
    }

    public Icon(String name, String fileName, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.fileName = fileName;
        this.description = description;
    }
}
