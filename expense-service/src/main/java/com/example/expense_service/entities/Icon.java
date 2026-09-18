package com.example.expense_service.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "icons")
public class Icon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "file_name", nullable = false, length = 100)
    private String fileName;

    @Column(length = 255)
    private String description;

    public Icon(String name, String fileName, String description) {
        this.name = name;
        this.fileName = fileName;
        this.description = description;
    }

}
