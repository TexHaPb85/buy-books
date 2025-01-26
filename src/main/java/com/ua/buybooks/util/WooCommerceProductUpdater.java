package com.ua.buybooks.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class WooCommerceProductUpdater {

    private static final String BASE_URL = "https://buy-books.com.ua/wp-json/wc/v3/products";
    private static final String CONSUMER_KEY = "ck_4897e18aee5567e7b07485599913a8f7ae57ec67";
    private static final String CONSUMER_SECRET = "cs_273159a7fff5b7ea1be157822d1e690c0683c518";

    public static void main(String[] args) {
        try {
            OkHttpClient client = new OkHttpClient();

            // Ідентифікатор товару, який потрібно оновити
            int productId = 270; // Замініть на реальний ID товару

            // Формування JSON для оновлення товару
            JsonObject productUpdate = new JsonObject();

            // Оновлення опису та короткого опису
            productUpdate.addProperty("description", "<p>Описание книги<br />\n" +
                "Почему компании теряют миллионы долларов каждый год? Одна из причин состоит в недопонимании своих клиентов. Незнание их потребностей, привычек и поведения приводит к уменьшению лояльности, оттоку покупателей и потере части выручки. Современные исследования показывают, что только 15% компаний хорошо изучили своих клиентов. Представьте себе: 85% компаний не используют потенциал своих данных для определения тенденций и улучшения отношений с клиентами! А в наших реалиях, когда частота покупок зависит от безопасности, наличия света, воды и интернета, без должного знания и адаптации бизнесы рискуют потерять позиции на рынке.</p>\n" +
                "\n" +
                "<p>К помощи станет эта книга, предлагающая: простые и эффективные инструменты клиентской аналитики; практические методы и стратегии оптимизации маркетинговых усилий и повышения прибыльности; опыт Airbnb, Amazon, Netflix и другие лидеры рынка. Пора превращать собственные данные в ценные знания, принимать обоснованные решения, адаптироваться к изменениям и побеждать конкурентов. Развивайте бизнес через глубокое понимание клиентов!</p>\n" +
                "\n" +
                "<p>Более 100 рисунков: черно-белые схемы и иллюстрации размещены в книге, а цветные доступны по qr-кодам.<br />\n" +
                "Практические таблицы и формулы для упрощения восприятия изложенного материала.<br />\n" +
                "Книга учитывает постковочные и военные реалии Украины, ведь автор уже более 15 лет работает с украинскими компаниями.</p>\n");
            productUpdate.addProperty("short_description", "<p>Оновлений короткий опис товару...</p>");

            // Оновлення мета-даних (SEO)
            JsonArray metaData = new JsonArray();

            JsonObject metaDescription = new JsonObject();
            metaDescription.addProperty("key", "_yoast_wpseo_metadesc");
            metaDescription.addProperty("value", "Оновлений мета-опис для SEO.");
            metaData.add(metaDescription);

            productUpdate.add("meta_data", metaData);

            // Оновлення alt для зображень
            JsonArray images = new JsonArray();
            JsonObject updatedImage = new JsonObject();
            updatedImage.addProperty("id", 268); // Замініть на ID зображення товару
            updatedImage.addProperty("alt", "Оновлений alt для зображення");
            images.add(updatedImage);

            productUpdate.add("images", images);

            // Авторизація
            String credentials = Credentials.basic(CONSUMER_KEY, CONSUMER_SECRET);

            // Формування HTTP-запиту
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), productUpdate.toString()
            );

            Request request = new Request.Builder()
                .url(BASE_URL + "/" + productId)
                .put(body)
                .addHeader("Authorization", credentials)
                .build();

            // Виконання запиту
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                System.out.println("Товар успішно оновлено: " + response.body().string());
            } else {
                System.err.println("Помилка: " + response.body().string());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
