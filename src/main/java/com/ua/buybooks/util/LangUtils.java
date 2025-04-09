package com.ua.buybooks.util;

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

}
