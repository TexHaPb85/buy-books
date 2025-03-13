package com.ua.buybooks.entity.wp;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWP {

    @Id
    @Column(name = "id")
    private Long id; // ID товару у WordPress (WooCommerce)

    @Column(name = "name_ua")
    private String nameUa; // Назва товару (укр.) → "Заголовок" (title)

    @Column(name = "name_ru")
    private String nameRu; // Назва товару (рос.) → "Заголовок" (title)

    @Column(name = "slug")
    private String slug; // URL-посилання → "Посилання" (slug)

    @Column(name = "type", nullable = false)
    private String type = "simple"; // Тип товару → "Тип" (type), за замовчуванням "simple"

    @Column(name = "regular_price")
    private Double regularPrice; // Звичайна ціна → "Звичайна ціна" (regular_price)

    @Column(name = "sale_price")
    private Double salePrice = null; // Акційна ціна → "Ціна зі знижкою" (sale_price), завжди null

    @Column(name = "sku")
    private String sku; // Артикул → "Артикул" (sku)

    @Column(name = "stock_status", nullable = false)
    private String stockStatus = "instock"; // Наявність → "У наявності" (stock_status: instock, outofstock)

    @Column(name = "description_ua", length = 10240)
    private String descriptionUa; // Опис (укр.) → "Опис" (description)

    @Column(name = "description_ru", length = 10240)
    private String descriptionRu; // Опис (рос.) → "Опис" (description)

    @Column(name = "short_description_ua", length = 4096)
    private String shortDescriptionUa; // Короткий опис (укр.) → "Короткий опис" (short_description)

    @Column(name = "short_description_ru", length = 4096)
    private String shortDescriptionRu; // Короткий опис (рос.) → "Короткий опис" (short_description)

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<CategoryWP> categories; // Категорії товару

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<TagWP> tags; // Теги товару

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<ImageWP> images; // Зображення товару

    @Column(name = "featured_image_id")
    private Long featuredImageId; // ID основного зображення → "Головне зображення" (featured_media)

    @Column(name = "post_status")
    private String postStatus; // Статус товару → "Статус" (publish, draft, pending, etc.)

    @Column(name = "post_type")
    private String postType; // Тип запису → "Тип запису" (product)

    // Yoast SEO Fields
    @Column(name = "yoast_seo_title")
    private String yoastSeoTitle; // SEO title (from product name or custom CSV field)

    @Column(name = "yoast_meta_description", length = 1024)
    private String yoastMetaDescription; // SEO Опис → "Meta description" (yoast_wpseo_metadesc)

    @Column(name = "yoast_focus_keyword")
    private String yoastFocusKeyword; // Ключове слово → "Focus Keyphrase" (yoast_wpseo_focuskw)

    @Column(name = "yoast_canonical_url")
    private String yoastCanonicalUrl; // Канонічний URL → "Canonical URL" (yoast_wpseo_canonical)

    @Column(name = "yoast_schema", length = 4096)
    private String yoastSchema; // JSON-LD Schema → "Schema" (yoast_wpseo_schema)

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Дата створення → "Дата публікації" (post_date)

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Дата оновлення → "Дата оновлення" (post_modified)

    @Column(name = "is_existing_in_wp", nullable = false)
    private Boolean isExistingInWP = false; // Флаг чи існує товар у WP чи це новий імпортований
}

