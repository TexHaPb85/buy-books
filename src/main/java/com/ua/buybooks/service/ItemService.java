package com.ua.buybooks.service;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import com.ua.buybooks.entity.FailureLog;
import com.ua.buybooks.entity.Item;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.repo.FailureLogRepository;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.util.DescriptionProcessingUtils;
import com.ua.buybooks.util.TransliterationUtil;
import com.ua.buybooks.util.constants.CountryCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor 
@Slf4j
public class ItemService {

    private final FailureLogRepository failureLogRepository;
    private final ItemWPRepository itemWPRepository;

//    public void processRecordsProm(List<CSVRecord> records) {
//        int newItems = 0;
//        int updatedItems = 0;
//        int conflicts = 0;
//
//        Map<String, List<ItemWP>> collect = itemWPRepository.findAll()
//            .stream()
//            .collect(Collectors.groupingBy(ItemWP::getNameRu));
//
//        for (CSVRecord record : records) {
//            try {
//                Long itemId = parseItemId(record);
//                String itemNameUa = record.get("Назва_позиції_укр").trim();
//                String itemNameRu = record.get("Назва_позиції").trim();
//                Optional<ItemWP> existingItemOpt = itemWPRepository.findByIdOrNameUaOrNameRu(itemId, itemNameUa, itemNameRu);
//
//                if (existingItemOpt.isPresent()) {
//                    ItemWP existingItem = existingItemOpt.get();
//                    existingItem = mapCsvToItem(record, existingItem);
//                    itemWPRepository.save(existingItem);
//                    updatedItems++;
//                } else {
//                    ItemWP newItem = mapCsvToItem(record, new ItemWP());
//                    itemWPRepository.save(newItem);
//                    newItems++;
//                }
//            } catch (Exception e) {
//                conflicts++;
//                log.error("⚠️ Import conflict: " + e.getMessage());
//            }
//        }
//
//        log.info("✅ Import completed: " + newItems + " new, " + updatedItems + " updated, " + conflicts + " conflicts.");
//    }

    private Long parseItemId(CSVRecord record) {
        String itemIdStr = record.get("Код_товару");
        if (itemIdStr.isBlank()) {
            return (long) record.get("Назва_позиції").hashCode();
        }
        return Long.valueOf(itemIdStr.trim());
    }

    private ItemWP mapCsvToItem(CSVRecord record, ItemWP item) {
        item.setId(parseItemId(record));
        item.setNameUa(record.get("Назва_позиції_укр").trim());
        item.setNameRu(record.get("Назва_позиції").trim());
        item.setDescriptionUa(record.get("Опис_укр"));
        item.setDescriptionRu(record.get("Опис"));
        item.setRegularPrice(parseDouble(record.get("Ціна")));
        item.setStockStatus(parseStockStatus(record.get("Наявність")));
        item.setSku(record.get("Ідентифікатор_товару"));
        item.setYoastMetaDescription(record.get("HTML_опис_укр"));
        item.setYoastFocusKeyword(record.get("Пошукові_запити_укр"));

        // Image handling
        String[] imageUrls = record.get("Посилання_зображення").split(",");
        item.setFeaturedImageId(parseImageId(imageUrls[0]));

        return item;
    }

    private Double parseDouble(String value) {
        try {
            return Double.valueOf(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String parseStockStatus(String availability) {
        return "Є в наявності".equalsIgnoreCase(availability) ? "instock" : "outofstock";
    }

    private Long parseImageId(String imageUrl) {
        if (imageUrl.isEmpty()) return null;
        return imageUrl.hashCode() * 1L;
    }

    public void processRecord(CSVRecord record) {
        // Map IDs
        String id1 = record.get("id_1");
        String id2 = record.get("id_2");
        if (!id1.equals(id2)) {
            throw new IllegalArgumentException("ID mismatch: " + id1 + " and " + id2);
        }
        Long id = Long.valueOf(id1);

        Item item = null;//repository.findById(id).orElse(new Item());
        item.setId(id);

        item.setNameRu(record.get("name_ru"));
        item.setNameUa(record.get("name_ua"));
        item.setModificationNameRu(record.get("modification_name_ru"));
        item.setModificationNameUa(record.get("modification_name_ua"));
        item.setAlias(TransliterationUtil.getTransliteratedAlias(record.get("alias"), item.getNameUa()));//TODO check
        item.setCategory(record.get("category"));
        item.setAdditionalCategories(record.get("additional_categories"));
        item.setPrice(Double.valueOf(record.get("price")));
        item.setIsVisible("да".equalsIgnoreCase(record.get("is_visible")));
        item.setIsAvailable("В наявності".equalsIgnoreCase(record.get("availability")));
        item.setImages(isLengthPermissible(record.get("images"), 4096, "images", id));
        item.setSupplier(record.get("supplier"));

        item.setDescriptionRu(isLengthPermissible(record.get("description_ru"), 10_240, "description_ru", id, CountryCode.RU.getCode(), item.getCategory()));
        item.setDescriptionUa(isLengthPermissible(record.get("description_ua"), 10_240, "description_ua", id, CountryCode.UA.getCode(), item.getCategory()));

        item.setShortDescriptionRu(isLengthPermissible(record.get("short_description_ru"), 4096, "short_description_ru", id, CountryCode.RU.getCode(), item.getCategory()));
        item.setShortDescriptionUa(isLengthPermissible(record.get("short_description_ua"), 4096, "short_description_ua", id, CountryCode.UA.getCode(), item.getCategory()));
        item.setColor(record.get("color"));
        item.setHtmlTitleRu(record.get("html_title_ru"));
        item.setHtmlTitleUa(record.get("html_title_ua"));
        item.setMetaKeywordsRu(record.get("meta_keywords_ru"));
        item.setMetaKeywordsUa(record.get("meta_keywords_ua"));
        item.setMetaDescriptionRu(record.get("meta_description_ru"));
        item.setMetaDescriptionUa(record.get("meta_description_ua"));

        //repository.save(item);
    }

    private String isLengthPermissible(String value, int maxLength, String columnName, Long id) {
        if (value != null && value.length() > maxLength) {
            String errorMessage = String.format("Value of column '%s' is too long for item %d", columnName, id);
            logFailure(id, null, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }

    private String isLengthPermissible(String value, int maxLength, String columnName, Long id, String language, String category) {
        if (value != null && value.length() > maxLength) {
            String errorMessage = String.format("Value of column '%s' is too long for item %d", columnName, id);
            logFailure(id, null, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        return language == CountryCode.UA.getCode() ? DescriptionProcessingUtils.processDescriptionUA(value, category) : DescriptionProcessingUtils.processDescriptionRU(value, category);
    }

    private void logFailure(Long itemId, String itemName, String failureReason) {
        FailureLog log = new FailureLog();
        log.setType(FailureLog.FailureType.IMPORT);
        log.setItemId(itemId);
        log.setItemName(itemName);
        log.setFailureReason(failureReason);
        failureLogRepository.save(log);
    }
}
