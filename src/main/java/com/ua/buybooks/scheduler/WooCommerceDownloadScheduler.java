package com.ua.buybooks.scheduler;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.entity.wp.ImageWP;
import com.ua.buybooks.entity.wp.ItemWP;
import com.ua.buybooks.entity.wp.TagWP;
import com.ua.buybooks.repo.wp.CategoryWPRepository;
import com.ua.buybooks.repo.wp.ImageWPRepository;
import com.ua.buybooks.repo.wp.ItemWPRepository;
import com.ua.buybooks.repo.wp.TagWPRepository;
import com.ua.buybooks.util.constants.CountryCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
@RequiredArgsConstructor 
@Slf4j
public class WooCommerceDownloadScheduler {

    private final OkHttpClient client;
    private final ItemWPRepository itemWPRepository;
    private final CategoryWPRepository categoryWPRepository;
    private final TagWPRepository tagWPRepository;
    private final ImageWPRepository imageWPRepository;
    @Value("${wc.api.base-url-woocommerce}")
    private String wcBaseUrl;
    @Value("${wc.api.consumer-key}")
    private String consumerKey;
    @Value("${wc.api.consumer-secret}")
    private String consumerSecret;
    @Value("${wp.admin.username}")
    private String wpAdminUsername;
    @Value("${wp.admin.app-password}")
    private String wpAdminAppPassword;

    @Value("${scheduler.import-enabled:false}")
    private boolean importEnabled;

    @Scheduled(fixedDelay = 60 * 60, initialDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void initWooData() {
        if (!importEnabled) {
            log.info("❌ WooCommerce data sync is disabled. Set 'scheduler.import-enabled' to true to enable.");
            return;
        }
        syncAllData();
    }

    public void syncAllData() {
        log.info("Starting WooCommerce data sync...");

        // 1. Load categories
        loadCategories();

        // 2. Load tags
        loadTags();

        // 3. Load media
        loadMedia();

        // 4. Load products
        loadItems();

        log.info("WooCommerce data sync completed.");
    }

    private void loadCategories() {
        int page = 1;
        while (true) {
            String url = wcBaseUrl + "/products/categories?per_page=100&page=" + page;
            JsonArray categoriesArray = fetchJsonArray(url);

            if (categoriesArray == null || categoriesArray.size() == 0) {
                break;
            }

            for (JsonElement element : categoriesArray) {
                JsonObject cat = element.getAsJsonObject();
                long id = cat.get("id").getAsLong();
                String name = cat.get("name").getAsString();
                try {
                    String locale = null;
                    Long translatedId = null;
                    // ✅ Get Locale (If available)
                    if (containsPolylangTranslations(cat)) {
                        try {
                            locale = cat.get("polylang_current_lang").getAsString();
                            translatedId = cat.get("polylang_translations").getAsJsonObject().get(CountryCode.RU.getCode()).getAsLong() == id
                                ? cat.get("polylang_translations").getAsJsonObject().get(CountryCode.UA.getCode()).getAsLong()
                                : cat.get("polylang_translations").getAsJsonObject().get(CountryCode.RU.getCode()).getAsLong();
                        } catch (Exception e) {
                            log.info("❌ Error selecting polylang translations for category " + name + " id:" + id
                                + " error:" + e.getClass() + ":" + e.getMessage());
                        }
                    } else {
                        log.info("❌ Warning:Category locale data is missing, " + name + " id:" + id);
                    }
                    categoryWPRepository.save(CategoryWP.builder()
                        .categoryId(id)
                        .categoryWordpressId(id)
                        .slug(cat.get("slug").getAsString())
                        .description(cat.get("description").getAsString())
                        .photoUri(cat.get("image").isJsonNull() ? null : cat.get("image").getAsJsonObject().get("src").getAsString())
                        .categoryName(name)
                        .locale(locale) // ✅ Set locale
                        .translatedCategoryId(translatedId) // ✅ Set translated version ID
                        .build());
                } catch (Exception e) {
                    log.info("❌ Error during importing of category " + name + " id:" + id);
                    e.printStackTrace();
                }
            }
            page++;
        }
    }

    private boolean containsPolylangTranslations(JsonObject obj) {
        try {
            return obj.has("polylang_current_lang")
                && obj.has("polylang_translations")
                && !obj.get("polylang_translations").getAsJsonObject().entrySet().isEmpty()
                && !obj.get("polylang_current_lang").getAsString().equalsIgnoreCase("false");
        } catch (IllegalStateException | NullPointerException e) {
            return false;
        }

    }

    private void loadTags() {
        int page = 1;
        while (true) {
            String url = wcBaseUrl + "/products/tags?per_page=100&page=" + page;
            JsonArray tagsArray = fetchJsonArray(url);

            if (tagsArray == null || tagsArray.size() == 0) {
                break;
            }

            for (JsonElement element : tagsArray) {
                JsonObject tag = element.getAsJsonObject();
                Long id = tag.get("id").getAsLong();
                String name = tag.get("name").getAsString();


                tagWPRepository.save(TagWP.builder()
                    .tagId(id)
                    .tagName(name)
//                    .locale(locale) // ✅ Set locale
//                    .translatedId(translatedId) // ✅ Set translated version ID
                    .build());
            }
            page++;
        }
    }

    private void loadItems() {
        int page = 1;
        while (true) {
            String url = wcBaseUrl + "/products?per_page=100&page=" + page;
            JsonArray itemsArray = fetchJsonArray(url);

            if (itemsArray == null || itemsArray.size() == 0) {
                break;
            }

            for (JsonElement element : itemsArray) {
                JsonObject prod = element.getAsJsonObject();
                int id = prod.get("id").getAsInt();
                String name = prod.has("name") ? prod.get("name").getAsString() : "";
                String slug = prod.has("slug") ? prod.get("slug").getAsString() : "";
                String descriptionUa = prod.has("description") ? prod.get("description").getAsString() : "";
                String shortDescriptionUa = prod.has("short_description") ? prod.get("short_description").getAsString() : "";
                String sku = prod.has("sku") ? prod.get("sku").getAsString() : null;
                Double price = prod.has("regular_price") ? prod.get("regular_price").getAsDouble() : null;

                // Stock status
                String stockStatus = prod.has("stock_status") ? prod.get("stock_status").getAsString() : "instock";

                // Categories
                List<CategoryWP> categories = new ArrayList<>();
                if (prod.has("categories") && prod.get("categories").isJsonArray()) {
                    JsonArray categoriesArray = prod.getAsJsonArray("categories");
                    for (JsonElement catElement : categoriesArray) {
                        JsonObject catObj = catElement.getAsJsonObject();
                        Long categoryId = catObj.get("id").getAsLong();
                        String categoryName = catObj.get("name").getAsString();
                        categories.add(CategoryWP.builder().categoryId(categoryId).categoryName(categoryName).build());
                    }
                }

                // Tags
                List<TagWP> tags = new ArrayList<>();
                if (prod.has("tags") && prod.get("tags").isJsonArray()) {
                    JsonArray tagsArray = prod.getAsJsonArray("tags");
                    for (JsonElement tagElement : tagsArray) {
                        JsonObject tagObj = tagElement.getAsJsonObject();
                        Long tagId = tagObj.get("id").getAsLong();
                        String tagName = tagObj.get("name").getAsString();
                        tags.add(TagWP.builder().tagId(tagId).tagName(tagName).build());
                    }
                }

                // Images
                List<ImageWP> images = new ArrayList<>();
                Long featuredImageId = null;

                if (prod.has("images") && prod.get("images").isJsonArray()) {
                    JsonArray imagesArray = prod.getAsJsonArray("images");

                    for (int i = 0; i < imagesArray.size(); i++) {
                        JsonObject imgObj = imagesArray.get(i).getAsJsonObject();

                        Long imageId = imgObj.has("id") ? imgObj.get("id").getAsLong() : null;
                        String fileUrl = imgObj.has("src") ? imgObj.get("src").getAsString() : "";
                        String altText = imgObj.has("alt") ? imgObj.get("alt").getAsString() : "";
                        String title = imgObj.has("title") ? imgObj.get("title").getAsString() : "";
                        String caption = imgObj.has("caption") ? imgObj.get("caption").getAsString() : "";
                        String description = imgObj.has("description") ? imgObj.get("description").getAsString() : "";

                        ImageWP image = ImageWP.builder()
                            .wpImageId(imageId)
                            .name(title)
                            .altText(altText)
                            .title(title)
                            .caption(caption)
                            .description(description)
                            .targetWpSiteUri(fileUrl)
                            .originalImageUri(null)
                            .build();
                        imageWPRepository.save(image);
                        images.add(image);

                        if (i == 0) {
                            featuredImageId = imageId; // First image is the featured image
                        }
                    }
                }

                // Yoast SEO metadata
                String yoastFocusKeyword = null, yoastMetaDescription = null, yoastCanonicalUrl = null, yoastSchema = null;
                if (prod.has("meta_data") && prod.get("meta_data").isJsonArray()) {
                    JsonArray metaDataArray = prod.getAsJsonArray("meta_data");
                    for (JsonElement metaElement : metaDataArray) {
                        JsonObject metaObj = metaElement.getAsJsonObject();
                        String key = metaObj.get("key").getAsString();
                        if (!metaObj.has("value") || metaObj.get("value").isJsonNull()) {
                            continue;
                        }

                        String value = metaObj.get("value").getAsString();
                        switch (key) {
                            case "_yoast_wpseo_focuskw":
                                yoastFocusKeyword = value;
                                break;
                            case "_yoast_wpseo_metadesc":
                                yoastMetaDescription = value;
                                break;
                            case "_yoast_wpseo_canonical":
                                yoastCanonicalUrl = value;
                                break;
                            case "_yoast_wpseo_schema":
                                yoastSchema = value;
                                break;
                        }
                    }
                }

                // Timestamps
                LocalDateTime createdAt = prod.has("date_created") ?
                    LocalDateTime.parse(prod.get("date_created").getAsString().replace("Z", "")) : null;
                LocalDateTime updatedAt = prod.has("date_modified") ?
                    LocalDateTime.parse(prod.get("date_modified").getAsString().replace("Z", "")) : null;

                // Save ItemWP entity
                ItemWP item = new ItemWP();
                item.setId((long) id);
                item.setItemWordpressId((long) id);
                item.setOriginalName(name);
                item.setSlug(slug);
                item.setDescriptionRu(descriptionUa);
                item.setShortDescriptionRu(shortDescriptionUa);
                item.setSku(sku);
                item.setRegularPrice(price);
                item.setStockStatus(stockStatus);
                item.setCategories(categories);
                item.setTags(tags);
                item.setImages(images);
                item.setFeaturedImageId(featuredImageId);
                item.setYoastFocusKeyword(yoastFocusKeyword);
                item.setYoastMetaDescription(yoastMetaDescription);
                item.setYoastCanonicalUrl(yoastCanonicalUrl);
                item.setYoastSchema(yoastSchema);
                item.setCreatedAt(createdAt);
                item.setUpdatedAt(updatedAt);
//                item.setLocale(locale); // ✅ Set locale
//                item.setTranslatedId(translatedId); // ✅ Set translated version

                itemWPRepository.save(item);
            }
            page++;
        }
    }

    private void loadMedia() {
        int page = 1;
        while (true) {
            String url = wcBaseUrl.replace("/wc/v3", "/wp/v2") + "/media?per_page=100&page=" + page;

            Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", Credentials.basic(wpAdminUsername, wpAdminAppPassword))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 401) {
                    log.error("❌ ERROR: Unauthorized (401). Check your WordPress App Password credentials.");
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    log.error("❌ ERROR: Failed to fetch media. Response code: " + response.code());
                    return;
                }

                JsonArray mediaArray = JsonParser.parseString(response.body().string()).getAsJsonArray();
                if (mediaArray.size() == 0) {
                    break;
                }

                for (JsonElement element : mediaArray) {
                    JsonObject mediaObj = element.getAsJsonObject();
                    Long imageId = mediaObj.get("id").getAsLong();
                    String fileUrl = mediaObj.get("source_url").getAsString();
                    String altText = mediaObj.has("alt_text") ? mediaObj.get("alt_text").getAsString() : "";
                    String title = mediaObj.has("title") ? mediaObj.get("title").getAsJsonObject().get("rendered").getAsString() : "";
                    String caption = mediaObj.has("caption") ? mediaObj.get("caption").getAsJsonObject().get("rendered").getAsString() : "";
                    String description = mediaObj.has("description") ? mediaObj.get("description").getAsJsonObject().get("rendered").getAsString() : "";

                    ImageWP image = ImageWP.builder()
                        .wpImageId(imageId)
                        .name(title)
                        .altText(altText)
                        .title(title)
                        .caption(caption)
                        .description(description)
                        .originalImageUri(null)
                        .targetWpSiteUri(fileUrl)
                        .build();
                    imageWPRepository.save(image);
                }
                page++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private JsonArray fetchJsonArray(String url) {
        try {
            Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return JsonParser.parseString(response.body().string()).getAsJsonArray();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
