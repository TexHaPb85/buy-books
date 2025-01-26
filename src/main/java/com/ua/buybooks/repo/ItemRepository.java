package com.ua.buybooks.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ua.buybooks.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}