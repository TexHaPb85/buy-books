package com.ua.buybooks.controller;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ua.buybooks.entity.Item;
import com.ua.buybooks.service.ItemService;
import com.ua.buybooks.service.WooCommerceProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final WooCommerceProductService wooCommerceProductService;

    @PostMapping("/upload-item/{itemId}")
    public void uploadItem(@PathVariable(name = "itemId") Item itemId) {
        wooCommerceProductService.uploadItemToWooCommerce(itemId);
    }

    @PostMapping("/import/horoshop")
    public String importItems(@RequestParam("file") MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(
                    "id_1", "id_2", "unused_column", "name_ru", "name_ua", "modification_name_ru", "modification_name_ua", "alias",
                    "unused_column_2", "category", "additional_categories", "price", "unused_price_column", "currency",
                    "is_visible", "availability", "images", "supplier", "description_ru", "description_ua", "short_description_ru",
                    "short_description_ua", "color", "html_title_ru", "html_title_ua", "meta_keywords_ru", "meta_keywords_ua",
                    "meta_description_ru", "meta_description_ua"
                )
                .withSkipHeaderRecord()
                .parse(reader);

            for (CSVRecord record : records) {
                try {
                    itemService.processRecord(record);
                } catch (IllegalArgumentException ex) {
                    System.out.println("->>>>>>>>>>>>>>>>Import failed: " + ex.getMessage());
                }
            }
            return "Import successful!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Import failed: " + e.getMessage()); // Mark the transaction as rollback-only
        }
    }
}
