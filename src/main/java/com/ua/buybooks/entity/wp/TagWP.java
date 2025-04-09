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
@Table(name = "tags_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "items")
public class TagWP {

    @Id
    @Column(name = "tag_id")
    private Long tagId; // ID тега у WordPress (має співпадати з WP)

    @Column(name = "tag_name", nullable = false)
    private String tagName; // Назва тега

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<ItemWP> items = new ArrayList<>(); // 🔄 Many-to-Many

    @Column(name = "locale")
    private String locale; // ✅ New: Language code (ru, uk)

    @Column(name = "translated_id")
    private Long translatedId; // ✅ New: ID of the translated product in WP
}


