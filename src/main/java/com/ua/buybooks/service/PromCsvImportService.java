package com.ua.buybooks.service;

import static com.ua.buybooks.util.LangUtils.normalizeText;
import static com.ua.buybooks.util.TransliterationUtil.transliterateRuToEn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.entity.wp.ImageWP;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.entity.wp.TagWP;
import com.ua.buybooks.repo.wp.CategoryWPRepository;
import com.ua.buybooks.repo.wp.ImageWPRepository;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.repo.wp.TagWPRepository;
import com.ua.buybooks.util.DescriptionProcessingUtils;
import com.ua.buybooks.util.LangUtils;
import com.ua.buybooks.util.TransliterationUtil;
import com.ua.buybooks.util.constants.CountryCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromCsvImportService {

    private static final double WORD_MATCH_THRESHOLD = 0.9; // 90% match required
    private final ItemWPRepository itemWPRepository;
    private final CategoryWPRepository categoryWPRepository;
    private final ImageWPRepository imageWPRepository;
    private final TagWPRepository tagWPRepository;

    private final Map<Long, ItemWP> itemCacheById = new ConcurrentHashMap<>();
    private final List<ItemWP> itemCacheByName = new ArrayList<>();

    private int numOfNewItems = 0;
    private int numOfUpdatedItems = 0;

    @Transactional
    public void processCategoriesProm(List<CSVRecord> records) {
        for (CSVRecord record : records) {

            List<CategoryWP> all = categoryWPRepository.findAll();
            CategoryWP existingCategoryUa = all.stream()
                .filter(categoryWP -> LangUtils.areCategoryNamesEqual(categoryWP.getCategoryName(), record.get("Назва_групи_укр")))
                .findFirst()
                .orElse(null);

            if (existingCategoryUa != null) {
                existingCategoryUa.setCategoryName(record.get("Назва_групи_укр"));
                existingCategoryUa.setSlug(TransliterationUtil.transliterateUaToEn(record.get("Назва_групи_укр")));
                existingCategoryUa.setDescription("Категорія товарів: " + record.get("Назва_групи_укр"));
                existingCategoryUa.setPhotoUri(record.get("Посилання_зображення_групи"));
                existingCategoryUa.setLocale(CountryCode.UA.getCode());
                existingCategoryUa.setDifferentFromWordpress(true);
            }

            CategoryWP categoryUa = existingCategoryUa != null ? existingCategoryUa : CategoryWP.builder()
                .categoryId(Long.valueOf(record.get("Ідентифікатор_групи")))
                .categoryName(record.get("Назва_групи_укр"))
                .slug(TransliterationUtil.transliterateUaToEn(record.get("Назва_групи_укр")))
                .description("Категорія товарів: " + record.get("Назва_групи_укр"))
                .photoUri(record.get("Посилання_зображення_групи"))
                .locale(CountryCode.UA.getCode())
                .isDifferentFromWordpress(true)
                .build();

            CategoryWP existingCategoryRu = all.stream()
                .filter(categoryWP -> LangUtils.areCategoryNamesEqual(categoryWP.getCategoryName(), record.get("Назва_групи")))
                .findFirst()
                .orElse(null);

            if (existingCategoryRu != null) {
                existingCategoryRu.setCategoryName(record.get("Назва_групи"));
                existingCategoryRu.setSlug(TransliterationUtil.transliterateRuToEn(record.get("Назва_групи")));
                existingCategoryRu.setDescription("Категория товаров: " + record.get("Назва_групи"));
                existingCategoryRu.setPhotoUri(record.get("Посилання_зображення_групи"));
                existingCategoryRu.setLocale(CountryCode.RU.getCode());
                existingCategoryRu.setDifferentFromWordpress(true);
            }

            CategoryWP categoryRu = existingCategoryRu != null ? existingCategoryRu : CategoryWP.builder()
                .categoryId(Long.valueOf(record.get("Номер_групи")))
                .categoryName(record.get("Назва_групи"))
                .slug(TransliterationUtil.transliterateRuToEn(record.get("Назва_групи")))
                .description("Категория товаров: " + record.get("Назва_групи"))
                .photoUri(record.get("Посилання_зображення_групи"))
                .locale(CountryCode.RU.getCode())
                .isDifferentFromWordpress(true)
                .build();

            if (LangUtils.isUaText(categoryUa.getCategoryName())) {
                categoryWPRepository.save(categoryUa);
            } else {
                System.out.println("❌ Warning: Category name is not in Ukrainian: " + categoryUa.getCategoryName());
            }

            if (LangUtils.isRuText(categoryRu.getCategoryName())) {
                categoryWPRepository.save(categoryRu);
            } else {
                System.out.println("❌ Warning: Category name is not in Russian: " + categoryRu.getCategoryName());
            }
        }
    }


    @Transactional
    public void processItemRecordsProm(List<CSVRecord> records) {
        System.out.println("🔄 Preloading items into cache...");
        preloadItemsFromDB();

        System.out.println("🚀 Processing " + records.size() + " records in parallel...");
//        ForkJoinPool customThreadPool = new ForkJoinPool(10); // Use 10 threads
//        customThreadPool.submit(() ->
//            records.parallelStream().forEach(this::processRecord)
//        ).join();

        records.forEach(this::processItemRecord);

        System.out.println("✅ Import completed.");
        System.out.println("🆕 New items: " + numOfNewItems);
        System.out.println("🔄 Updated items: " + numOfUpdatedItems);
        numOfNewItems = 0;
        numOfUpdatedItems = 0;
    }

    private void preloadItemsFromDB() {
        List<ItemWP> items = itemWPRepository.findAll();
        items.forEach(item -> {
            itemCacheById.put(item.getId(), item);
            itemCacheByName.add(item);
        });
        System.out.println("✅ Cached " + items.size() + " items from database.");
    }

    private void processCategoryRecord(CSVRecord record) {
        try {
            Long catId = parseItemId(record);
            String catName = record.get("Назва_групи");

            CategoryWP categoryWP = categoryWPRepository.findByCategoryIdAndCategoryName(catId, catName)
                .orElse(new CategoryWP());
            categoryWP.setCategoryId(catId);
            categoryWP.setCategoryName(catName);
            categoryWP.setLocale("uk");

        } catch (Exception e) {
            System.err.println("❌ Error processing record: " + record.get("Назва_позиції") + " error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processItemRecord(CSVRecord record) {
        try {
            Long itemId = parseItemId(record);
            String normalizedItemNameRu = normalizeText(record.get("Назва_позиції"));

            // 1️⃣ Search by ID
            ItemWP matchedItem = findMatchingItemById(itemId, normalizedItemNameRu);

            // 2️⃣ If no exact match by ID, search by name similarity
            if (matchedItem == null) {
                matchedItem = findMatchingItemByName(normalizedItemNameRu);
            }

            // 3️⃣ If match found, update; otherwise, insert as new item
            createOrUpdateItem(matchedItem, record);
        } catch (Exception e) {
            System.err.println("❌ Error processing record: " + record.get("Назва_позиції") + " error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ItemWP findMatchingItemById(Long id, String nameRu) {
        ItemWP item = itemCacheById.get(id);
        if (item != null) {
            if (wordMatchPercentage(item.getNameRu(), nameRu) >= WORD_MATCH_THRESHOLD) {
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
        if (nameRu == null) {
            return null;
        }
        for (ItemWP item : itemCacheByName) {
            if (item.getNameRu() == null) {
                System.err.println("⚠️ Warning: Item name is null in DB for item ID: " + item.getId());
                continue;
            }
            if (wordMatchPercentage(item.getNameRu(), nameRu) >= WORD_MATCH_THRESHOLD
                && hasMatchingCategoryOrImage(item, nameRu)) {
                logPartialMatch(item, nameRu);
                System.out.println("⚠️⚠️⚠️ Partial match found: " + item.getId()
                    + " - Existing: " + item.getNameRu()
                    + " | New: " + nameRu);
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

    private void createOrUpdateItem(ItemWP itemToProcess, CSVRecord record) {
        String itemName = record.get("Назва_позиції");
        String loggingPrefix = "🚀 Inserting new item: ";
        if (itemToProcess != null) {
            itemName = itemToProcess.getNameRu();
            loggingPrefix = "🔄 Updating existing item: ";
            numOfUpdatedItems++;
        } else {
            numOfNewItems++;
        }
        System.out.println(loggingPrefix + itemName);

        ItemWP newItem = itemToProcess != null ? itemToProcess : new ItemWP();
        newItem.setId(parseItemId(record));
        newItem.setNameRu(itemName);
        newItem.setNameUa(record.get("Назва_позиції_укр"));
        String transliteratedRuName = transliterateRuToEn(itemName);
        newItem.setSlug(transliteratedRuName);
        newItem.setRegularPrice(Double.valueOf(record.get("Ціна")));
        newItem.setSku(newItem.getId() + "-" + transliterateRuToEn(itemName));
        newItem.setStockStatus("+".equalsIgnoreCase(record.get("Наявність")) ? "instock" : "outofstock");
        newItem.setDescriptionRu(DescriptionProcessingUtils.processDescriptionRU(record.get("Опис"), record.get("Назва_групи")));
        newItem.setDescriptionUa(DescriptionProcessingUtils.processDescriptionUA(record.get("Опис_укр"), record.get("Назва_групи")));
        newItem.setShortDescriptionRu(record.get("Опис"));
        newItem.setShortDescriptionUa(record.get("Опис_укр"));

        String categoryName = record.get("Назва_групи");
        CategoryWP categoryWP = categoryWPRepository.findByCategoryName(categoryName).orElseGet(() -> {
            throw new RuntimeException("Category not found: " + categoryName);
//            CategoryWP newCategory = CategoryWP.builder()
//                .categoryName(record.get("Назва_групи"))
//                .build();
//            return categoryWPRepository.save(newCategory);
        });
        newItem.setCategories(List.of(categoryWP));

        List<TagWP> tags = new ArrayList<>();
        String[] splitedCategoryWords = categoryWP.getCategoryName().trim().split(",\\s*");// Split by comma and trim spaces
        for (String word : splitedCategoryWords) {
            final String normalizeWord = normalizeText(word);
            TagWP tag = tagWPRepository.findById((long) normalizeWord.hashCode()).orElseGet(() -> {
                TagWP newTag = TagWP.builder()
                    .tagId((long) normalizeWord.hashCode())
                    .tagName(normalizeWord)
                    .build();
                return tagWPRepository.save(newTag);
            });
            tags.add(tagWPRepository.save(tag));
        }
        newItem.setTags(tags);

        String imageUrlsString = record.get("Посилання_зображення").trim();
        String[] imageUrls = imageUrlsString.split(",\\s*"); // Split by comma and trim spaces
        List<ImageWP> images = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Long imageId = extractImageId(imageUrl);
            if (imageId == null) {
                continue; // Skip invalid URLs
            }

            // Construct the image metadata for SEO
            String imageName = transliteratedRuName + "-" + imageId + ".jpg"; // Ensure filename format
            String altText = "Зображення для товару:" + itemName;
            String title = itemName + " | buy-books.com.ua";
            String caption = "Придбати " + itemName + " в категорії " + categoryWP.getCategoryName();
            String description = "Купуйте " + itemName + " в онлайн-магазині книг buy-books.com.ua";

            ImageWP saved = imageWPRepository.findById(imageId).orElseGet(() -> {
                ImageWP newImage = ImageWP.builder()
                    .wpImageId(imageId)
                    .name(imageName)
                    .altText(altText)
                    .title(title)
                    .caption(caption)
                    .description(description)
                    .originalImageUri(imageUrl)
                    .targetWpSiteUri(null)
                    .build();
                return imageWPRepository.save(newImage);
            });
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

    private Long extractImageId(String imageUrl) {
        Pattern IMAGE_ID_PATTERN = Pattern.compile("https://images\\.prom\\.ua/(\\d+)_.*");
        Matcher matcher = IMAGE_ID_PATTERN.matcher(imageUrl);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1));
        }
        System.err.println("⚠️ Warning: Could not extract image ID from URL: " + imageUrl);
        return null;
    }

    private double wordMatchPercentage(String text1, String text2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(normalizeText(text1).split(" ")));
        Set<String> words2 = new HashSet<>(Arrays.asList(normalizeText(text2).split(" ")));

        long commonWords = words1.stream().filter(words2::contains).count();
        return (double) commonWords / Math.max(words1.size(), words2.size());
    }

    private Long parseItemId(CSVRecord record) {
        String itemIdStr = record.get("Унікальний_ідентифікатор").trim();
        if (itemIdStr.isBlank()) {
            return (long) record.get("Назва_позиції").hashCode();
        }
        return Long.valueOf(itemIdStr);
    }
}
