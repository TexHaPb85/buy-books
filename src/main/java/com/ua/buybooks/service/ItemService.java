package com.ua.buybooks.service;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import com.ua.buybooks.entity.FailureLog;
import com.ua.buybooks.entity.Item;
import com.ua.buybooks.repo.FailureLogRepository;
import com.ua.buybooks.repo.ItemRepository;
import com.ua.buybooks.util.DescriptionProcessingUtils;
import com.ua.buybooks.util.TransliterationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repository;
    private final FailureLogRepository failureLogRepository;

    public void processRecord(CSVRecord record) {
        // Map IDs
        String id1 = record.get("id_1");
        String id2 = record.get("id_2");
        if (!id1.equals(id2)) {
            throw new IllegalArgumentException("ID mismatch: " + id1 + " and " + id2);
        }
        Long id = Long.valueOf(id1);

        Item item = repository.findById(id).orElse(new Item());
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

        item.setDescriptionRu(isLengthPermissible(record.get("description_ru"), 10_240, "description_ru", id, "ru", item.getCategory()));
        item.setDescriptionUa(isLengthPermissible(record.get("description_ua"), 10_240, "description_ua", id, "ua", item.getCategory()));

        item.setShortDescriptionRu(isLengthPermissible(record.get("short_description_ru"), 4096, "short_description_ru", id, "ru", item.getCategory()));
        item.setShortDescriptionUa(isLengthPermissible(record.get("short_description_ua"), 4096, "short_description_ua", id, "ua", item.getCategory()));
        item.setColor(record.get("color"));
        item.setHtmlTitleRu(record.get("html_title_ru"));
        item.setHtmlTitleUa(record.get("html_title_ua"));
        item.setMetaKeywordsRu(record.get("meta_keywords_ru"));
        item.setMetaKeywordsUa(record.get("meta_keywords_ua"));
        item.setMetaDescriptionRu(record.get("meta_description_ru"));
        item.setMetaDescriptionUa(record.get("meta_description_ua"));

        repository.save(item);
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
        return language == "ua" ? DescriptionProcessingUtils.processDescriptionUA(value, category) : DescriptionProcessingUtils.processDescriptionRU(value, category);
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
