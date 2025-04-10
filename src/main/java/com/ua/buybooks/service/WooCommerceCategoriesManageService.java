package com.ua.buybooks.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.ua.buybooks.entity.wp.CategoryWP;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor 
@Slf4j
public class WooCommerceCategoriesManageService {
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

    public void deleteCategoryFromWooCommerce(Long categoryWpId) {
        if (categoryWpId == null) {
            log.error("❌ Category ID is null. Cannot delete.");
            return;
        }

        try {
            String url = wcBaseUrl + "/products/categories/" + categoryWpId + "?force=true";

            Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .addHeader("Content-Type", "application/json")
                .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                log.info("✅ Category ID " + categoryWpId + " deleted successfully.");
            } else {
                log.error("❌ Failed to delete category ID " + categoryWpId + ". Error: " + responseBody);
            }

        } catch (IOException e) {
            log.error("❌ Exception while deleting category ID " + categoryWpId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void uploadCategoryToWooCommerce(CategoryWP categoryToUpload) {
        try {
            boolean isUpdate = categoryToUpload.getCategoryWordpressId() != null;
            String url = wcBaseUrl + "/products/categories";

            JsonObject categoryJson = new JsonObject();
            categoryJson.addProperty("name", categoryToUpload.getCategoryName());

            if (categoryToUpload.getSlug() != null) {
                categoryJson.addProperty("slug", categoryToUpload.getSlug());
            }

            if (categoryToUpload.getDescription() != null) {
                categoryJson.addProperty("description", categoryToUpload.getDescription());
            }

            if (categoryToUpload.getParentCategoryId() != null) {
                categoryJson.addProperty("parent", categoryToUpload.getParentCategoryId());
            }

            if (categoryToUpload.getLocale() != null) {
                categoryJson.addProperty("lang", categoryToUpload.getLocale()); // ✅ For Polylang Pro
            }

            // 🔹 Optional: Image upload (Woo expects image ID, not URL)
//            if (categoryToUpload.getPhotoUri() != null) {
//                try {
//                    long imageId = Long.parseLong(categoryToUpload.getPhotoUri());
//                    JsonObject imageJson = new JsonObject();
//                    imageJson.addProperty("id", imageId);
//                    categoryJson.add("image", imageJson);
//                } catch (NumberFormatException e) {
//                    log.error("⚠️ Skipping photoUri: not a valid image ID: " + categoryToUpload.getPhotoUri());
//                }
//            }

            // 🔹 Prepare request body string and log it
            String jsonBodyString = categoryJson.toString();
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonBodyString
            );

            Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .addHeader("Content-Type", "application/json");

            if (isUpdate) {
                url += "/" + categoryToUpload.getCategoryWordpressId();
                requestBuilder.url(url).put(body); // ✅ Update
            } else {
                requestBuilder.url(url).post(body); // ✅ Create
            }

            // 🔹 Send request
            Request request = requestBuilder.build();
            log.info("🔄 Sending request to WooCommerce: " + request.method() + " " + request.url());
            log.info("📦 Payload: " + jsonBodyString);

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                log.info("✅ Category '" + categoryToUpload.getCategoryName() + "' uploaded successfully.");
            } else {
                log.error("❌ Failed to upload category '" + categoryToUpload.getCategoryName() + "'. Error: " + responseBody);
            }

        } catch (IOException e) {
            log.error("❌ Exception while uploading category: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
