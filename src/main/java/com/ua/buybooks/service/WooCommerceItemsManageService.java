package com.ua.buybooks.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ua.buybooks.entity.FailureLog;
import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.entity.wp.ImageWP;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.entity.wp.TagWP;
import com.ua.buybooks.repo.FailureLogRepository;
import com.ua.buybooks.util.constants.CountryCode;

import lombok.RequiredArgsConstructor;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
public class WooCommerceItemsManageService {

    private final FailureLogRepository failureLogRepository;
    private final DownloadService downloadService;
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
                translations.addProperty(item.getLocale().equals(CountryCode.RU.getCode()) ? CountryCode.UA.getCode() : CountryCode.RU.getCode(), item.getTranslatedId());
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

    public void deleteProductAndImagesFromWooCommerce(Long productId) {
        try {
            String productUrl = wcBaseUrl + "/products/" + productId;
            Request getProductRequest = new Request.Builder()
                .url(productUrl)
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .get()
                .build();

            Response getResponse = okHttpClient.newCall(getProductRequest).execute();
            String productBody = getResponse.body().string();

            if (!getResponse.isSuccessful()) {
                System.err.println("❌ Failed to fetch product " + productId + " before deletion. Response: " + productBody);
                return;
            }

            JsonObject productJson = JsonParser.parseString(productBody).getAsJsonObject();

            // ✅ Step 1: Extract image IDs
            List<Long> imageIds = new ArrayList<>();
            if (productJson.has("images")) {
                JsonArray images = productJson.getAsJsonArray("images");
                for (JsonElement imageElement : images) {
                    JsonObject image = imageElement.getAsJsonObject();
                    if (image.has("id")) {
                        imageIds.add(image.get("id").getAsLong());
                    }
                }
            }

            // ✅ Step 2: Delete product
            HttpUrl deleteProductUrl = HttpUrl.parse(productUrl).newBuilder()
                .addQueryParameter("force", "true")
                .build();

            Request deleteProductRequest = new Request.Builder()
                .url(deleteProductUrl)
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .delete()
                .build();

            Response deleteResponse = okHttpClient.newCall(deleteProductRequest).execute();
            String deleteProductResponse = deleteResponse.body().string();

            if (deleteResponse.isSuccessful()) {
                System.out.println("🗑️ Product " + productId + " deleted successfully.");
            } else {
                System.err.println("❌ Failed to delete product " + productId + ". Response: " + deleteProductResponse);
            }

            // ✅ Step 3: Delete associated images
            for (Long imageId : imageIds) {
                String mediaUrl = wpBaseUrl + "/media/" + imageId + "?force=true";
                Request deleteImageRequest = new Request.Builder()
                    .url(mediaUrl)
                    .addHeader("Authorization", Credentials.basic(wpAdminUsername, wpAdminAppPassword))
                    .delete()
                    .build();

                Response imageResponse = okHttpClient.newCall(deleteImageRequest).execute();
                String imageResponseBody = imageResponse.body().string();

                if (imageResponse.isSuccessful()) {
                    System.out.println("🗑️ Deleted image ID " + imageId);
                } else {
                    System.err.println("⚠️ Failed to delete image ID " + imageId + ". Response: " + imageResponseBody);
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Exception while deleting product or images: " + e.getMessage());
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
