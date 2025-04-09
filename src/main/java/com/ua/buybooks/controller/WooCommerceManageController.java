package com.ua.buybooks.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.repo.wp.CategoryWPRepository;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.service.WooCommerceManageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/woo-commerce")
@RequiredArgsConstructor
public class WooCommerceManageController {

    private final WooCommerceManageService wooCommerceManageService;
    private final ItemWPRepository itemWPRepository;
    private final CategoryWPRepository categoryWPRepository;

    @GetMapping("/upload-item/{wpItemId}")
    public ItemWP uploadItemToWooCommerce(@PathVariable Long wpItemId) {
        ItemWP itemToUpload = itemWPRepository
            .findById(wpItemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        wooCommerceManageService.uploadItemToWooCommerce(itemToUpload);
        return itemToUpload;
    }

    @GetMapping("/upload-category/{wpCategoryId}")
    public CategoryWP uploadCategoryToWooCommerce(@PathVariable Long wpCategoryId) {
        CategoryWP categoryWP = categoryWPRepository
            .findById(wpCategoryId)
            .orElseThrow(() -> new RuntimeException("wpCategory not found"));
        wooCommerceManageService.uploadCategoryToWooCommerce(categoryWP);
        return categoryWP;
    }

    @GetMapping("/remove-category/{wpCategoryId}")
    public CategoryWP removeCategoryFromWooCommerce(@PathVariable Long wpCategoryId) {
        CategoryWP categoryWP = categoryWPRepository
            .findById(wpCategoryId)
            .orElseThrow(() -> new RuntimeException("wpCategory not found"));
        wooCommerceManageService.deleteCategoryFromWooCommerce(wpCategoryId);
        return categoryWP;
    }

    @GetMapping("/upload-category/all")
    public void uploadAllCategories() {
        List<CategoryWP> all = categoryWPRepository.findAll();
        for (CategoryWP categoryWP : all) {
            wooCommerceManageService.uploadCategoryToWooCommerce(categoryWP);
        }
    }
}
