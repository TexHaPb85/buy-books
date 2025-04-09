package com.ua.buybooks.entity.wp;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "categories_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "items")
public class CategoryWP {

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_wp_id")
    private Long categoryWordpressId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "slug")
    private String slug;

    @Column(name = "photo_uri")
    private String photoUri;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<ItemWP> items = new ArrayList<>();

    @Column(name = "locale")
    private String locale;

    @Column(name = "translated_id")
    private Long translatedCategoryId;

    @Column(name = "parent_id")
    private Long parentCategoryId;

    @Column(name = "is_different_from_wordpress")
    private boolean isDifferentFromWordpress;
}

