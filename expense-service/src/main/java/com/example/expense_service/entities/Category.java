package com.example.expense_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_user", columnList = "user_id"),
        @Index(name = "idx_category_parent", columnList = "parent_id")
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id", nullable = false, foreignKey = @ForeignKey(name = "fk_category_icon"))
    private Icon icon;

    // Parent category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_category_parent"))
    private Category parent;

    // Child categories
    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    public Category(String name, Icon icon) {
        this.userId = null;
        this.name = name;
        this.icon = icon;
        this.parent = null;
    }

    public Category(UUID userId, String name, Icon icon, Category parent) {
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.parent = parent;
    }

}
