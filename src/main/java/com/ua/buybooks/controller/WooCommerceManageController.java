package com.ua.buybooks.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.repo.wp.CategoryWPRepository;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.service.WooCommerceCategoriesManageService;
import com.ua.buybooks.service.WooCommerceItemsManageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/woo-commerce")
@RequiredArgsConstructor
@Slf4j
public class WooCommerceManageController {

    private final WooCommerceCategoriesManageService wooCommerceCategoriesManageService;
    private final WooCommerceItemsManageService wooCommerceItemsManageService;

    private final ItemWPRepository itemWPRepository;
    private final CategoryWPRepository categoryWPRepository;

    // ----------------------------Item related methods----------------------------
    @GetMapping("/upload-item/{wpItemId}")
    public ItemWP uploadItemToWooCommerce(@PathVariable Long wpItemId) {
        ItemWP itemToUpload = itemWPRepository
            .findById(wpItemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        wooCommerceItemsManageService.uploadItemToWooCommerce(itemToUpload);
        return itemToUpload;
    }

    @GetMapping("/upload-item/all")
    public void uploadAllItemsToWooCommerce() {
        List<ItemWP> all = itemWPRepository.findAll();
        for (ItemWP itemToUpload : all) {
            wooCommerceItemsManageService.uploadItemToWooCommerce(itemToUpload);
        }
    }

    @DeleteMapping("/remove-item/{wpItemId}")
    public ItemWP removeItemFromWooCommerce(@PathVariable Long wpItemId) {
        ItemWP itemToDelete = itemWPRepository
            .findById(wpItemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        wooCommerceItemsManageService.deleteProductAndImagesFromWooCommerce(wpItemId);
        return itemToDelete;
    }


    // ----------------------------Category related methods----------------------------
    @GetMapping("/upload-category/{wpCategoryId}")
    public CategoryWP uploadCategoryToWooCommerce(@PathVariable Long wpCategoryId) {
        CategoryWP categoryWP = categoryWPRepository
            .findById(wpCategoryId)
            .orElseThrow(() -> new RuntimeException("wpCategory not found"));
        wooCommerceCategoriesManageService.uploadCategoryToWooCommerce(categoryWP);
        return categoryWP;
    }

    @GetMapping("/upload-category/all")
    public void uploadAllCategories() {
        List<CategoryWP> all = categoryWPRepository.findAll();
        for (CategoryWP categoryWP : all) {
            wooCommerceCategoriesManageService.uploadCategoryToWooCommerce(categoryWP);
        }
    }

    @DeleteMapping("/remove-category/{wpCategoryId}")
    public CategoryWP removeCategoryFromWooCommerce(@PathVariable Long wpCategoryId) {
        CategoryWP categoryWP = categoryWPRepository
            .findById(wpCategoryId)
            .orElseThrow(() -> new RuntimeException("wpCategory not found"));
        wooCommerceCategoriesManageService.deleteCategoryFromWooCommerce(wpCategoryId);
        return categoryWP;
    }
}
