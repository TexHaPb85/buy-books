package com.ua.buybooks.entity.wp;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "original_image_uri", nullable = false)
    private String originalImageUri; // Посилання на файл
}

