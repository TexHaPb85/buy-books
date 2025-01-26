package com.ua.buybooks.util;

import okhttp3.*;
import com.google.gson.*;

public class WooCommerceProductImporter {

    private static final String BASE_URL = "https://buy-books.com.ua/wp-json/wc/v3/products";
    private static final String CONSUMER_KEY = "ck_4897e18aee5567e7b07485599913a8f7ae57ec67";
    private static final String CONSUMER_SECRET = "cs_273159a7fff5b7ea1be157822d1e690c0683c518";

    public static void main(String[] args) {
        try {
            OkHttpClient client = new OkHttpClient();

            // Формування JSON для товару
            JsonObject product = new JsonObject();
            product.addProperty("name", "Клієнтська аналітика");
            product.addProperty("slug", "kliientska-analityka");
            product.addProperty("type", "simple");
            product.addProperty("regular_price", "450.00");
            product.addProperty("description", "<p>Опис товару...</p>");
            product.addProperty("short_description", "<p>Короткий опис товару...</p>");
            product.addProperty("sku", "CA001");

            // Категорії
            JsonArray categories = new JsonArray();
            JsonObject category = new JsonObject();
            category.addProperty("id", 10);
            categories.add(category);
            product.add("categories", categories);

            // Теги
            JsonArray tags = new JsonArray();
            JsonObject tag = new JsonObject();
            tag.addProperty("id", 5);
            tags.add(tag);
            product.add("tags", tags);

            // Додаємо зображення до товару
            JsonArray images = new JsonArray();
            String[] imageUrls = {
                "https://psibooks.com.ua/content/images/31/kliientska-analityka.-yak-zrozumity-pokuptsiv-pidvyshchyty-yikhniu-loialnist-i-zbilshyty-dokhody-chubukova-iryna-11224570866641_+50321a5e95.jpg",
                "https://psibooks.com.ua/content/images/31/kliientska-analityka.-yak-zrozumity-pokuptsiv-pidvyshchyty-yikhniu-loialnist-i-zbilshyty-dokhody-chubukova-iryna-94735915587835_+0a0b4a9d07.jpg"
            };
            int position = 0;
            for (String imageUrl : imageUrls) {
                JsonObject image = new JsonObject();
                image.addProperty("src", imageUrl);
                image.addProperty("alt", "Зображення товару");
                image.addProperty("position", position++);
                images.add(image);
            }
            product.add("images", images);

            // Додаємо мета-дані для SEO
            JsonArray metaData = new JsonArray();
            JsonObject focusKeyword = new JsonObject();
            focusKeyword.addProperty("key", "_yoast_wpseo_focuskw");
            focusKeyword.addProperty("value", "аналітика клієнтів");
            metaData.add(focusKeyword);

            JsonObject seoTitle = new JsonObject();
            seoTitle.addProperty("key", "_yoast_wpseo_title");
            seoTitle.addProperty("value", "Клієнтська аналітика - SEO Назва");
            metaData.add(seoTitle);

            JsonObject metaDescription = new JsonObject();
            metaDescription.addProperty("key", "_yoast_wpseo_metadesc");
            metaDescription.addProperty("value", "Ця книга допоможе зрозуміти клієнтів і покращити ваш бізнес.");
            metaData.add(metaDescription);

            product.add("meta_data", metaData);

            // Додаємо локалізації (Polylang)
            JsonObject translations = new JsonObject();

            JsonObject ukTranslation = new JsonObject();
            ukTranslation.addProperty("name", "Клієнтська аналітика");
            ukTranslation.addProperty("description", "<p>Опис українською...</p>");
            ukTranslation.addProperty("short_description", "<p>Короткий опис українською...</p>");
            translations.add("uk", ukTranslation);

            JsonObject ruTranslation = new JsonObject();
            ruTranslation.addProperty("name", "Клиентская аналитика");
            ruTranslation.addProperty("description", "<p>Описание на русском...</p>");
            ruTranslation.addProperty("short_description", "<p>Краткое описание на русском...</p>");
            translations.add("ru", ruTranslation);

            product.add("translations", translations);

            // Авторизація
            String credentials = Credentials.basic(CONSUMER_KEY, CONSUMER_SECRET);

            // Формування HTTP-запиту
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), product.toString()
            );

            Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("Authorization", credentials)
                .build();

            // Виконання запиту
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                System.out.println("Товар успішно імпортовано: " + response.body().string());
            } else {
                System.err.println("Помилка: " + response.body().string());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
