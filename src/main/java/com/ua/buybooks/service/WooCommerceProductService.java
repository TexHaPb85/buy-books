/*
package com.ua.buybooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ua.buybooks.entity.FailureLog;
import com.ua.buybooks.entity.Item;
import com.ua.buybooks.repo.FailureLogRepository;
import com.ua.buybooks.util.DescriptionProcessingUtils;

import lombok.RequiredArgsConstructor;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
public class WooCommerceProductService {

    @Value("${wc.api.base-url}")
    private String wcBaseUrl; //https://buy-books.com.ua/wp-json/wc/v3

    @Value("${wc.api.consumer-key}")
    private String consumerKey;

    @Value("${wc.api.consumer-secret}")
    private String consumerSecret;

    private final FailureLogRepository failureLogRepository;

    public void uploadItemToWooCommerce(Item item) {
        try {
            OkHttpClient client = new OkHttpClient();
            JsonObject product = new JsonObject();

            // Map item fields to WooCommerce product
            product.addProperty("name", item.getNameRu());
            product.addProperty("slug", item.getAlias()); // Use alias as slug
            product.addProperty("type", "simple");
            product.addProperty("regular_price", item.getPrice().toString());

            // Process descriptions using StringUtils_CST methods
            String processedDescriptionRu = DescriptionProcessingUtils.processDescriptionRU(item.getDescriptionRu(), item.getCategory());
            String processedDescriptionUa = DescriptionProcessingUtils.processDescriptionUA(item.getDescriptionUa(), item.getCategory());

            product.addProperty("description", processedDescriptionRu);
            product.addProperty("short_description", item.getShortDescriptionRu());
            product.addProperty("sku", "SKU-" + item.getId());

            // Categories
            JsonArray categories = new JsonArray();
            JsonObject category = new JsonObject();
            int categoryId = getCategoryID(item.getCategory());
            category.addProperty("id", categoryId); // Dynamic category ID
            categories.add(category);
            product.add("categories", categories);

            // Tags
            JsonArray tags = new JsonArray();
            JsonObject tag = new JsonObject();
            tag.addProperty("id", 5); // Replace with actual tag ID
            tags.add(tag);
            product.add("tags", tags);

            // Images
            String[] imageUrls = item.getImages().split(";");
            JsonArray images = new JsonArray();

            for (String imageUrl : imageUrls) {
                JsonObject image = new JsonObject();
                image.addProperty("src", imageUrl.trim());
                image.addProperty("alt", "Зображення для товару: " + item.getNameUa()); // Use item name for alt
                images.add(image);
            }

            product.add("images", images);

            // Meta data (SEO)
            JsonArray metaData = new JsonArray();

            JsonObject focusKeyword = new JsonObject();
            focusKeyword.addProperty("key", "_yoast_wpseo_focuskw");
            focusKeyword.addProperty("value", item.getNameRu()); // Use item name as focus keyword
            metaData.add(focusKeyword);

            JsonObject metaDescription = new JsonObject();
            metaDescription.addProperty("key", "_yoast_wpseo_metadesc");
            metaDescription.addProperty("value", item.getMetaDescriptionUa()); // Use UA meta description
            metaData.add(metaDescription);

            product.add("meta_data", metaData);

            // Translations (Polylang)
            JsonObject translations = new JsonObject();

            JsonObject ukTranslation = new JsonObject();
            ukTranslation.addProperty("name", item.getNameUa());
            ukTranslation.addProperty("description", processedDescriptionUa);
            ukTranslation.addProperty("short_description", item.getShortDescriptionUa());
            translations.add("uk", ukTranslation);

            JsonObject ruTranslation = new JsonObject();
            ruTranslation.addProperty("name", item.getNameRu());
            ruTranslation.addProperty("description", processedDescriptionRu);
            ruTranslation.addProperty("short_description", item.getShortDescriptionRu());
            translations.add("ru", ruTranslation);

            product.add("translations", translations);

            // Authorization
            String credentials = Credentials.basic(consumerKey, consumerSecret);

            // HTTP request
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), product.toString()
            );

            Request request = new Request.Builder()
                .url(wcBaseUrl + "/products")
                .post(body)
                .addHeader("Authorization", credentials)
                .build();
            System.out.println("Generated JSON Payload: " + product.toString());
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                logFailure(item.getId(), item.getNameRu(), response.body().string());
            }
        } catch (Exception e) {
            logFailure(item.getId(), item.getNameRu(), e.getMessage());
            e.printStackTrace();
        }
    }

    private int getCategoryID(String categoryName) {
        // Implement logic to retrieve category ID from WooCommerce based on the category name.
        // You can store and reuse IDs after the first retrieval to optimize performance.
        // For now, return a placeholder value.
        return 10;
    }
    private void logFailure(Long itemId, String itemName, String failureReason) {
        FailureLog log = new FailureLog();
        log.setType(FailureLog.FailureType.UPLOAD);
        log.setItemId(itemId);
        log.setItemName(itemName);
        log.setFailureReason(failureReason);
        failureLogRepository.save(log);
    }
}

*/
