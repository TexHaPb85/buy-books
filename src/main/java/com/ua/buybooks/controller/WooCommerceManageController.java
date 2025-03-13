package com.ua.buybooks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.service.WooCommerceManageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/woo-commerce")
@RequiredArgsConstructor
public class WooCommerceManageController {

    private final WooCommerceManageService wooCommerceManageService;
    private final ItemWPRepository itemWPRepository;

    @GetMapping("/upload-item/{wpItemId}")
    public ItemWP uploadItemToWooCommerce(@PathVariable Long wpItemId) {
        ItemWP itemToUpload = itemWPRepository
            .findById(wpItemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        wooCommerceManageService.uploadItemToWooCommerce(itemToUpload);
        return itemToUpload;
    }
}
