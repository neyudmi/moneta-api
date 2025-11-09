package com.example.expense_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_category_group"))
    private CategoryGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_category_icon"))
    private Icon icon;

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID userId;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public Category() {
        this.id = UUID.randomUUID();
    }

}
