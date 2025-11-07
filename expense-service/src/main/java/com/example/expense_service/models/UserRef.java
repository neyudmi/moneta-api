package com.example.expense_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "user_ref")
public class UserRef {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public UserRef() {
    }

    public UserRef(UUID userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
