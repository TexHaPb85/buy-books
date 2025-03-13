package com.ua.buybooks.entity.wp;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWP {

    @Id
    @Column(name = "category_id")
    private Long categoryId; // ID категорії у WordPress (має співпадати з WP)

    @Column(name = "category_name", nullable = false)
    private String categoryName; // Назва категорії
}
