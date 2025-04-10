package com.ua.buybooks.util;

import java.text.Normalizer;

import com.ua.buybooks.util.constants.CountryCode;

public class LangUtils {

    public static CountryCode getLangByText(String text) {
        if (isUaText(text)) {
            return CountryCode.UA;
        } else if (isRuText(text)) {
            return CountryCode.RU;
        }
        return null;
    }
    public static boolean isUaText(String text) {
        return text != null && text.matches(".*[а-яА-Я].*");
    }

    public static boolean isRuText(String text) {
        return text != null && text.matches(".*[а-яА-Я].*");
    }

    public static boolean isUaCategory(String category) {
        String uaCategoryName = "Книги на Украинском";
        return category.equalsIgnoreCase(uaCategoryName);
    }

    public static boolean areCategoryNamesEqual(String name1, String name2) {
        return normalizeText(name1).equalsIgnoreCase(normalizeText(name2));
    }

    public static String normalizeText(String text) {
        return Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD)
            .replaceAll("\\p{Punct}", " ") // Remove punctuation
            .replaceAll("\\s+", " ") // Replace multiple spaces with a single space
            .trim();
    }
}
