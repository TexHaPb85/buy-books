package com.ua.buybooks.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ua.buybooks.entity.wp.CategoryWP;
import com.ua.buybooks.repo.FailureLogRepository;
import com.ua.buybooks.repo.wp.ImageWPRepository;
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
public class DownloadService {

    @Value("${wc.api.base-url-woocommerce}")
    private String wcBaseUrl; //https://buy-books.com.ua/wp-json/wc/v3

    @Value("${wc.api.consumer-key}")
    private String consumerKey;

    @Value("${wc.api.consumer-secret}")
    private String consumerSecret;

    private final FailureLogRepository failureLogRepository;
    private final ImageWPRepository imageWPRepository;

    private final OkHttpClient okHttpClient;

    public List<CategoryWP> downLoadCategories() {
        int page = 1;
        List<CategoryWP> categories = new ArrayList<>();
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

                    CategoryWP build = CategoryWP.builder()
                        .categoryId(id)
                        .categoryWordpressId(id)
                        .categoryName(name)
                        .locale(locale) // ✅ Set locale
                        .translatedCategoryId(translatedId) // ✅ Set translated version ID
                        .build();
                    categories.add(build);
                } catch (Exception e) {
                    log.info("❌ Error during importing of category " + name + " id:" + id);
                    e.printStackTrace();
                }
            }
            page++;
        }
        return categories;
    }

    private JsonArray fetchJsonArray(String url) {
        try {
            Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return JsonParser.parseString(response.body().string()).getAsJsonArray();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
