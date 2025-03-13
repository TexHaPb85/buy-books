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
@Table(name = "tags_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagWP {

    @Id
    @Column(name = "tag_id")
    private Long tagId; // ID тега у WordPress (має співпадати з WP)

    @Column(name = "tag_name", nullable = false)
    private String tagName; // Назва тега

    @ManyToMany(mappedBy = "tags")
    private List<ItemWP> items = new ArrayList<>(); // 🔄 Many-to-Many
}


