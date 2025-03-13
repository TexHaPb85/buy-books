package com.ua.buybooks.service;

import static com.ua.buybooks.util.TransliterationUtil.transliterateRuToEn;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.entity.wp.ImageWP;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.repo.wp.CategoryWPRepository;
import com.ua.buybooks.repo.wp.ImageWPRepository;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.util.DescriptionProcessingUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromCsvImportService {

    private static final double WORD_MATCH_THRESHOLD = 0.7; // 70% match required
    private final ItemWPRepository itemWPRepository;
    private final CategoryWPRepository categoryWPRepository;
    private final ImageWPRepository imageWPRepository;

    private final Map<Long, ItemWP> itemCacheById = new ConcurrentHashMap<>();
    private final List<ItemWP> itemCacheByName = new ArrayList<>();

    public void processRecordsProm(List<CSVRecord> records) {
        System.out.println("🔄 Preloading items into cache...");
        preloadItemsFromDB();

        System.out.println("🚀 Processing " + records.size() + " records in parallel...");
        ForkJoinPool customThreadPool = new ForkJoinPool(10); // Use 10 threads
        customThreadPool.submit(() ->
            records.parallelStream().forEach(this::processRecord)
        ).join();

        System.out.println("✅ Import completed.");
    }

    private void preloadItemsFromDB() {
        List<ItemWP> items = itemWPRepository.findAll();
        items.forEach(item -> {
            itemCacheById.put(item.getId(), item);
            itemCacheByName.add(item);
        });
        System.out.println("✅ Cached " + items.size() + " items from database.");
    }

    private void processRecord(CSVRecord record) {
        try {
            Long itemId = parseItemId(record);
            String itemNameRu = normalizeText(record.get("Назва_позиції"));

            // 1️⃣ Search by ID
            ItemWP matchedItem = findMatchingItemById(itemId, itemNameRu);

            // 2️⃣ If no exact match by ID, search by name similarity
            if (matchedItem == null) {
                matchedItem = findMatchingItemByName(itemNameRu);
            }

            // 3️⃣ If match found, update; otherwise, insert as new item
            if (matchedItem != null) {
                updateExistingItem(matchedItem, record);
            } else {
                insertNewItem(record);
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ItemWP findMatchingItemById(Long id, String nameRu) {
        ItemWP item = itemCacheById.get(id);
        if (item != null) {
            if (wordMatchPercentage(item.getNameRu(), nameRu) >= 1.0) {
                return item; // ✅ Exact name match
            } else if (wordMatchPercentage(item.getNameRu(), nameRu) >= WORD_MATCH_THRESHOLD
                && hasMatchingCategoryOrImage(item, nameRu)) {
                logPartialMatch(item, nameRu);
                return item; // ✅ Partial match with category/image check
            }
        }
        return null;
    }

    private ItemWP findMatchingItemByName(String nameRu) {
        for (ItemWP item : itemCacheByName) {
            if (wordMatchPercentage(item.getNameRu(), nameRu) >= WORD_MATCH_THRESHOLD
                && hasMatchingCategoryOrImage(item, nameRu)) {
                logPartialMatch(item, nameRu);
                return item;
            }
        }
        return null;
    }

    private void logPartialMatch(ItemWP item, String newItemName) {
        System.out.println("⚠️ Partial match found: " + item.getId()
            + " - Existing: " + item.getNameRu()
            + " | New: " + newItemName);
    }

    private boolean hasMatchingCategoryOrImage(ItemWP item, String nameRu) {
        return item.getCategories().stream()
            .anyMatch(cat -> wordMatchPercentage(cat.getCategoryName(), nameRu) >= WORD_MATCH_THRESHOLD)
            || item.getImages().stream()
            .anyMatch(img -> wordMatchPercentage(img.getName(), nameRu) >= WORD_MATCH_THRESHOLD);
    }

    @Transactional
    public void updateExistingItem(ItemWP item, CSVRecord record) {
        System.out.println("🔄 Updating existing item: " + item.getId());

        itemWPRepository.save(item);
    }

    @Transactional
    public void insertNewItem(CSVRecord record) {
        String itemName = record.get("Назва_позиції");
        System.out.println("🆕 Inserting new item: " + itemName);
        ItemWP newItem = new ItemWP();
        newItem.setId(parseItemId(record));
        newItem.setNameRu(itemName);
        newItem.setNameUa(record.get("Назва_позиції_укр"));
        String transliteratedRuName = transliterateRuToEn(itemName);
        newItem.setSlug(transliteratedRuName);
        newItem.setRegularPrice(Double.valueOf(record.get("Ціна")));
        newItem.setSku(newItem.getId() + "-" + transliterateRuToEn(itemName));
        newItem.setStockStatus(parseStockStatus(record.get("Наявність")));
        newItem.setDescriptionRu(DescriptionProcessingUtils.processDescriptionRU(record.get("Опис"), record.get("Категорія")));
        newItem.setDescriptionUa(DescriptionProcessingUtils.processDescriptionUA(record.get("Опис_укр"), record.get("Категорія")));
        newItem.setShortDescriptionRu(record.get("Опис"));
        newItem.setShortDescriptionUa(record.get("Опис_укр"));

        Long categoryId = Long.parseLong(record.get("Номер_групи"));
        CategoryWP categoryWP = categoryWPRepository.findById(categoryId).orElseGet(() -> {
            CategoryWP newCategory = new CategoryWP(categoryId, record.get("Назва_групи"));
            return categoryWPRepository.save(newCategory);
        });

        newItem.setCategories(List.of(categoryWP));

        String imageUrlsString = record.get("Посилання_зображення").trim();
        String[] imageUrls = imageUrlsString.split(",\\s*"); // Split by comma and trim spaces
        List<ImageWP> images = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Long imageId = extractImageId(imageUrl);
            if (imageId == null) continue; // Skip invalid URLs

            // Construct the image metadata for SEO
            String imageName = transliteratedRuName + "-" + imageId + ".jpg"; // Ensure filename format
            String altText = itemName + " - купити в Україні";
            String title = itemName + " | buy-books.com.ua";
            String caption = "Придбати " + itemName + " в категорії " + categoryWP.getCategoryName();
            String description = "Купуйте " + itemName + " в онлайн-магазині книг buy-books.com.ua";

            ImageWP saved = imageWPRepository.save(new ImageWP(imageId, imageName, altText, title, caption, description, imageUrl));
            images.add(saved);
        }
        newItem.setFeaturedImageId(images.getFirst().getWpImageId());
        newItem.setImages(images);

        newItem.setPostStatus("publish");
        newItem.setPostType("product");
        //yoast seo:
        newItem.setYoastSeoTitle(itemName);
        newItem.setYoastFocusKeyword(record.get("Пошукові_запити"));
        newItem.setYoastMetaDescription(DescriptionProcessingUtils.addDescriptionEndingRu(itemName, categoryWP.getCategoryName()));
        //newItem.setYoastCanonicalUrl();// if you have multiple posts or pages with similar content, it tells the search engines what URL contains the original content
        //newItem.setYoastSchema();


        itemWPRepository.save(newItem);
    }

    private static Long extractImageId(String imageUrl) {
        Pattern IMAGE_ID_PATTERN = Pattern.compile("https://images\\.prom\\.ua/(\\d+)_.*");
        Matcher matcher = IMAGE_ID_PATTERN.matcher(imageUrl);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1));
        }
        System.err.println("⚠️ Warning: Could not extract image ID from URL: " + imageUrl);
        return null;
    }
    private String parseStockStatus(String availability) {
        return "Є в наявності".equalsIgnoreCase(availability) ? "instock" : "outofstock";
    }

    private double wordMatchPercentage(String text1, String text2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(normalizeText(text1).split(" ")));
        Set<String> words2 = new HashSet<>(Arrays.asList(normalizeText(text2).split(" ")));

        long commonWords = words1.stream().filter(words2::contains).count();
        return (double) commonWords / Math.max(words1.size(), words2.size());
    }

    private String normalizeText(String text) {
        return Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD)
            .replaceAll("\\p{Punct}", " ") // Remove punctuation
            .replaceAll("\\s+", " ") // Replace multiple spaces with a single space
            .trim();
    }

    private Long parseItemId(CSVRecord record) {
        String itemIdStr = record.get("Унікальний_ідентифікатор").trim();
        if (itemIdStr.isBlank()) {
            return (long) record.get("Назва_позиції").hashCode();
        }
        return Long.valueOf(itemIdStr);
    }
}
