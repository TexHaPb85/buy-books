package com.ua.buybooks.service;

import static com.ua.buybooks.util.DescriptionProcessingUtils.getOpositeLocale;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ua.buybooks.entity.FailureLog;
import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.entity.wp.ImageWP;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.entity.wp.TagWP;
import com.ua.buybooks.repo.FailureLogRepository;

import lombok.RequiredArgsConstructor;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
public class WooCommerceManageService {

    private final FailureLogRepository failureLogRepository;
    private final WooCommerceDownloadService wooCommerceDownloadService;
    private final OkHttpClient okHttpClient;

    @Value("${wc.api.base-url-woocommerce}")
    private String wcBaseUrl; //https://buy-books.com.ua/wp-json/wc/v3
    @Value("${wc.api.base-url-wp}")
    private String wpBaseUrl; //https://buy-books.com.ua/wp-json/wp/v2

    @Value("${wc.api.consumer-key}")
    private String consumerKey;
    @Value("${wc.api.consumer-secret}")
    private String consumerSecret;

    @Value("${wp.admin.username}")
    private String wpAdminUsername;
    @Value("${wp.admin.app-password}")
    private String wpAdminAppPassword;

    public void uploadCategoryToWooCommerce(CategoryWP categoryToUpload) {
        try {
            boolean isUpdate = categoryToUpload.getCategoryWordpressId() != null; // ✅ Check categoryWordpressId instead of categoryId
            String url = wcBaseUrl + "/products/categories"; // ✅ Correct base URL

            JsonObject categoryJson = new JsonObject();
            categoryJson.addProperty("name", categoryToUpload.getCategoryName());

            if (categoryToUpload.getParentCategoryId() != null) {
                categoryJson.addProperty("parent", categoryToUpload.getParentCategoryId());
            }

            if (categoryToUpload.getLocale() != null) {
                categoryJson.addProperty("lang", categoryToUpload.getLocale());
            }

            if (categoryToUpload.getTranslatedCategoryId() != null) {
//                {
//                    "key": "_pll_translation_group",
//                    "value": 70
//                }
                JsonArray metaData = new JsonArray();
                JsonObject translationMeta = new JsonObject();
                translationMeta.addProperty("key", "_pll_translation_group");
                translationMeta.addProperty("value", categoryToUpload.getTranslatedCategoryId());
                metaData.add(translationMeta);
                categoryJson.add("meta_data", metaData);

//               "translations":{"ru":70}
//                JsonObject translations = new JsonObject();
//                translations.addProperty(categoryToUpload.getLocale(), categoryToUpload.getTranslatedCategoryId()); // 🔥 Use correct locale dynamically
//                categoryJson.add("translations", translations);
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), categoryJson.toString());
            Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .addHeader("Content-Type", "application/json");

            if (isUpdate) {
                url += "/" + categoryToUpload.getCategoryWordpressId(); // ✅ Use categoryWordpressId for updates
                requestBuilder.url(url).put(body); // ✅ Update existing category
                updateCategoryTranslation(
                    categoryToUpload.getCategoryWordpressId(),
                    getOpositeLocale(categoryToUpload.getLocale()),
                    categoryToUpload.getTranslatedCategoryId());
            } else {
                requestBuilder.url(url).post(body); // ✅ Create new category
            }

            Request request = requestBuilder.build();
            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                System.out.println("✅ Category " + categoryToUpload.getCategoryName() + " uploaded successfully!");
            } else {
                System.err.println("❌ Failed to upload category " + categoryToUpload.getCategoryName() + ". Error: " + responseBody);
            }
        } catch (IOException e) {
            System.err.println("❌ Exception while uploading category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateCategoryTranslation(Long categoryId, String locale, Long translatedCategoryId) {
        try {
            String url = wpBaseUrl + "/wp/v2/categories/" + categoryId;

            JsonObject translationJson = new JsonObject();
            JsonObject translations = new JsonObject();
            translations.addProperty(locale, translatedCategoryId); // ✅ Example: "ru": 70
            translationJson.add("translations", translations);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), translationJson.toString());
            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", Credentials.basic(wpAdminUsername, wpAdminAppPassword))
                //.addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                System.out.println("✅ Translation updated for category " + categoryId);
            } else {
                System.err.println("❌ Failed to update translation for category " + categoryId + ". Error: " + responseBody);
            }
        } catch (IOException e) {
            System.err.println("❌ Exception while updating translation: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean deleteCategoryFromWooCommerce(Long categoryWordpressId) {
        if (categoryWordpressId == null) {
            System.err.println("❌ Cannot delete category: categoryWordpressId is null.");
            return false;
        }

        try {
            String deleteUrl = wcBaseUrl + "/products/categories/" + categoryWordpressId + "?force=true"; // Force delete
            String credentials = Credentials.basic(consumerKey, consumerSecret);

            Request deleteRequest = new Request.Builder()
                .url(deleteUrl)
                .delete()
                .addHeader("Authorization", credentials)
                .build();

            Response deleteResponse = okHttpClient.newCall(deleteRequest).execute();

            if (deleteResponse.isSuccessful()) {
                System.out.println("✅ Category deleted successfully (ID: " + categoryWordpressId + ")");
                return true;
            } else {
                System.err.println("❌ Failed to delete category (ID: " + categoryWordpressId + "). Error: " + deleteResponse.body().string());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting category (ID: " + categoryWordpressId + "): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void uploadItemToWooCommerce(ItemWP item) {
        try {
            JsonObject product = new JsonObject();

            // ✅ Basic Product Data
            product.addProperty("name", item.getNameRu()); // Upload only RU version
            product.addProperty("slug", item.getSlug());
            product.addProperty("type", item.getType());
            product.addProperty("regular_price", item.getRegularPrice().toString());
            product.addProperty("sku", item.getSku());
            product.addProperty("stock_status", item.getStockStatus());

            // ✅ Descriptions
            product.addProperty("description", item.getDescriptionRu());
            product.addProperty("short_description", item.getShortDescriptionRu());

            // ✅ Categories
            JsonArray categories = new JsonArray();
            for (CategoryWP category : item.getCategories()) {
                if (!category.getLocale().equals(item.getLocale())) {
                    throw new IllegalArgumentException("Item locale and category locale mismatch");
                }
                JsonObject categoryJson = new JsonObject();
                categoryJson.addProperty("id", category.getCategoryId());
                categoryJson.addProperty("name", category.getCategoryName());
                categories.add(categoryJson);
            }
            product.add("categories", categories);

            // ✅ Tags
            JsonArray tags = new JsonArray();
            for (TagWP tag : item.getTags()) {
                if (!tag.getLocale().equals(item.getLocale())) {
                    throw new IllegalArgumentException("Item locale and tag locale mismatch");
                }
                JsonObject tagJson = new JsonObject();
                tagJson.addProperty("id", tag.getTagId());
                tagJson.addProperty("name", tag.getTagName());
                tags.add(tagJson);
            }
            product.add("tags", tags);

            // ✅ Images
            JsonArray images = new JsonArray();
            for (ImageWP image : item.getImages()) {
                JsonObject imageJson = new JsonObject();

                if (image.getTargetWpSiteUri() != null) {
                    // ✅ Image already exists in WP, use ID instead of src
                    imageJson.addProperty("id", image.getWpImageId());
                } else {
                    // 🆕 New image, let WooCommerce fetch it from the original source
                    imageJson.addProperty("src", image.getOriginalImageUri());
                }

                // ✅ Add full metadata
                imageJson.addProperty("alt", image.getAltText());
                imageJson.addProperty("name", image.getName());
                imageJson.addProperty("title", image.getTitle());
                imageJson.addProperty("caption", image.getCaption());
                imageJson.addProperty("description", image.getDescription());

                images.add(imageJson);
            }
            product.add("images", images);

            if (item.getFeaturedImageId() != null) {
                product.addProperty("featured_media", item.getFeaturedImageId());
            }

            // ✅ Polylang Translations
            JsonObject translations = new JsonObject();
            if (item.getTranslatedId() != null) {
                translations.addProperty(item.getLocale().equals("ru") ? "uk" : "ru", item.getTranslatedId());
            }
            product.add("translations", translations);

            // ✅ Yoast SEO Meta Data
            JsonArray metaData = new JsonArray();
            addMetaData(metaData, "_yoast_wpseo_title", item.getYoastSeoTitle());
            addMetaData(metaData, "_yoast_wpseo_metadesc", item.getYoastMetaDescription());
            addMetaData(metaData, "_yoast_wpseo_focuskw", item.getYoastFocusKeyword());
            addMetaData(metaData, "_yoast_wpseo_canonical", item.getYoastCanonicalUrl());
            addMetaData(metaData, "_yoast_wpseo_schema", item.getYoastSchema());
            product.add("meta_data", metaData);

            // ✅ HTTP Request
            String credentials = Credentials.basic(consumerKey, consumerSecret);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), product.toString());

            Request request = new Request.Builder()
                .url(wcBaseUrl + "/products")
                .post(body)
                .addHeader("Authorization", credentials)
                .build();

            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                logFailure(item.getId(), item.getNameRu(), response.body().string());
            }
        } catch (Exception e) {
            logFailure(item.getId(), item.getNameRu(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMetaData(JsonArray metaData, String key, String value) {
        if (value != null && !value.isEmpty()) {
            JsonObject meta = new JsonObject();
            meta.addProperty("key", key);
            meta.addProperty("value", value);
            metaData.add(meta);
        }
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
