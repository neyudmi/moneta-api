package com.example.expense_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "category_groups")
public class CategoryGroup {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "icon_id")
    private Icon icon;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public CategoryGroup() {
    }

    public CategoryGroup(UUID id, String name, Icon icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }
}
