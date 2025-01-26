package com.ua.buybooks.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    private Long id;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_ua")
    private String nameUa;

    @Column(name = "modification_name_ru")
    private String modificationNameRu;

    @Column(name = "modification_name_ua")
    private String modificationNameUa;

    @Column(name = "alias")
    private String alias;

    @Column(name = "category")
    private String category;

    @Column(name = "additional_categories")
    private String additionalCategories;

    @Column(name = "price")
    private Double price;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "availability")
    private Boolean isAvailable;

    @Column(name = "images", length = 4096)
    private String images;

    @Column(name = "supplier")
    private String supplier;

    @Column(name = "description_ru", length = 10_240)
    private String descriptionRu;

    @Column(name = "description_ua", length = 10_240)
    private String descriptionUa;

    @Column(name = "short_description_ru", length = 4096)
    private String shortDescriptionRu;

    @Column(name = "short_description_ua", length = 4096)
    private String shortDescriptionUa;

    @Column(name = "color")
    private String color;

    @Column(name = "html_title_ru")
    private String htmlTitleRu;

    @Column(name = "html_title_ua")
    private String htmlTitleUa;

    @Column(name = "meta_keywords_ru", length = 1024)
    private String metaKeywordsRu;

    @Column(name = "meta_keywords_ua", length = 1024)
    private String metaKeywordsUa;

    @Column(name = "meta_description_ru", length = 1024)
    private String metaDescriptionRu;

    @Column(name = "meta_description_ua", length = 1024)
    private String metaDescriptionUa;
}

