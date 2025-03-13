package com.ua.buybooks.entity.wp;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tags_wp")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagWP {

    @Id
    @Column(name = "tag_id")
    private Long tagId; // ID тега у WordPress (має співпадати з WP)

    @Column(name = "tag_name", nullable = false)
    private String tagName; // Назва тега
}


