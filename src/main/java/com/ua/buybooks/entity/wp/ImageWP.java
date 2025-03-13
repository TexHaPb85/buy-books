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
@Table(name = "images_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageWP {

    @Id
    @Column(name = "wp_image_id")
    private Long wpImageId; // ID зображення у WordPress (має співпадати з WP)

    @Column(name = "name", nullable = false)
    private String name; // Назва файлу

    @Column(name = "alt_text")
    private String altText; // Альтернативний текст

    @Column(name = "title")
    private String title; // Заголовок

    @Column(name = "caption")
    private String caption; // Підпис

    @Column(name = "description")
    private String description; // Опис

    @Column(name = "original_image_uri")
    private String originalImageUri; // Посилання на файл

    @Column(name = "target_wp_site_uri")
    private String targetWpSiteUri; // ✅ New field → URI of the image on the WP site (if already uploaded)

    @ManyToMany(mappedBy = "images")
    private List<ItemWP> items = new ArrayList<>(); // 🔄 Many-to-Many
}

