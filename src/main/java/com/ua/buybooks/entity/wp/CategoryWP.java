package com.ua.buybooks.entity.wp;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "categories_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryWP {

    @Id
    @Column(name = "category_id")
    private Long categoryId; // ID категорії у WordPress (має співпадати з WP)

    @Column(name = "category_name", nullable = false)
    private String categoryName; // Назва категорії

    @ManyToMany(mappedBy = "categories")
    private List<ItemWP> items = new ArrayList<>(); // 🔄 Many-to-Many
}
