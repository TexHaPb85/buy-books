package com.ua.buybooks.util;

import java.util.HashMap;
import java.util.Map;

public class TransliterationUtil {

    // A simple mapping from Ukrainian letters to their English transliteration
    private static final Map<Character, String> UA_TO_EN_MAP = new HashMap<>();
    private static final Map<Character, String> RU_TO_EN_MAP = new HashMap<>();


    static {
        // Uppercase mapping
        UA_TO_EN_MAP.put('А', "A");
        UA_TO_EN_MAP.put('Б', "B");
        UA_TO_EN_MAP.put('В', "V");
        UA_TO_EN_MAP.put('Г', "H");
        UA_TO_EN_MAP.put('Ґ', "G");
        UA_TO_EN_MAP.put('Д', "D");
        UA_TO_EN_MAP.put('Е', "E");
        UA_TO_EN_MAP.put('Є', "Ye");
        UA_TO_EN_MAP.put('Ж', "Zh");
        UA_TO_EN_MAP.put('З', "Z");
        UA_TO_EN_MAP.put('И', "Y");
        UA_TO_EN_MAP.put('І', "I");
        UA_TO_EN_MAP.put('Ї', "Yi");
        UA_TO_EN_MAP.put('Й', "Y");
        UA_TO_EN_MAP.put('К', "K");
        UA_TO_EN_MAP.put('Л', "L");
        UA_TO_EN_MAP.put('М', "M");
        UA_TO_EN_MAP.put('Н', "N");
        UA_TO_EN_MAP.put('О', "O");
        UA_TO_EN_MAP.put('П', "P");
        UA_TO_EN_MAP.put('Р', "R");
        UA_TO_EN_MAP.put('С', "S");
        UA_TO_EN_MAP.put('Т', "T");
        UA_TO_EN_MAP.put('У', "U");
        UA_TO_EN_MAP.put('Ф', "F");
        UA_TO_EN_MAP.put('Х', "Kh");
        UA_TO_EN_MAP.put('Ц', "Ts");
        UA_TO_EN_MAP.put('Ч', "Ch");
        UA_TO_EN_MAP.put('Ш', "Sh");
        UA_TO_EN_MAP.put('Щ', "Shch");
        UA_TO_EN_MAP.put('Ю', "Yu");
        UA_TO_EN_MAP.put('Я', "Ya");

        // Lowercase mapping
        UA_TO_EN_MAP.put('а', "a");
        UA_TO_EN_MAP.put('б', "b");
        UA_TO_EN_MAP.put('в', "v");
        UA_TO_EN_MAP.put('г', "h");
        UA_TO_EN_MAP.put('ґ', "g");
        UA_TO_EN_MAP.put('д', "d");
        UA_TO_EN_MAP.put('е', "e");
        UA_TO_EN_MAP.put('є', "ye");
        UA_TO_EN_MAP.put('ж', "zh");
        UA_TO_EN_MAP.put('з', "z");
        UA_TO_EN_MAP.put('и', "y");
        UA_TO_EN_MAP.put('і', "i");
        UA_TO_EN_MAP.put('ї', "yi");
        UA_TO_EN_MAP.put('й', "y");
        UA_TO_EN_MAP.put('к', "k");
        UA_TO_EN_MAP.put('л', "l");
        UA_TO_EN_MAP.put('м', "m");
        UA_TO_EN_MAP.put('н', "n");
        UA_TO_EN_MAP.put('о', "o");
        UA_TO_EN_MAP.put('п', "p");
        UA_TO_EN_MAP.put('р', "r");
        UA_TO_EN_MAP.put('с', "s");
        UA_TO_EN_MAP.put('т', "t");
        UA_TO_EN_MAP.put('у', "u");
        UA_TO_EN_MAP.put('ф', "f");
        UA_TO_EN_MAP.put('х', "kh");
        UA_TO_EN_MAP.put('ц', "ts");
        UA_TO_EN_MAP.put('ч', "ch");
        UA_TO_EN_MAP.put('ш', "sh");
        UA_TO_EN_MAP.put('щ', "shch");
        UA_TO_EN_MAP.put('ю', "yu");
        UA_TO_EN_MAP.put('я', "ya");

        // Uppercase mapping
        RU_TO_EN_MAP.put('А', "A");
        RU_TO_EN_MAP.put('Б', "B");
        RU_TO_EN_MAP.put('В', "V");
        RU_TO_EN_MAP.put('Г', "G");
        RU_TO_EN_MAP.put('Д', "D");
        RU_TO_EN_MAP.put('Е', "E");
        RU_TO_EN_MAP.put('Ё', "Yo");
        RU_TO_EN_MAP.put('Ж', "Zh");
        RU_TO_EN_MAP.put('З', "Z");
        RU_TO_EN_MAP.put('И', "I");
        RU_TO_EN_MAP.put('Й', "Y");
        RU_TO_EN_MAP.put('К', "K");
        RU_TO_EN_MAP.put('Л', "L");
        RU_TO_EN_MAP.put('М', "M");
        RU_TO_EN_MAP.put('Н', "N");
        RU_TO_EN_MAP.put('О', "O");
        RU_TO_EN_MAP.put('П', "P");
        RU_TO_EN_MAP.put('Р', "R");
        RU_TO_EN_MAP.put('С', "S");
        RU_TO_EN_MAP.put('Т', "T");
        RU_TO_EN_MAP.put('У', "U");
        RU_TO_EN_MAP.put('Ф', "F");
        RU_TO_EN_MAP.put('Х', "Kh");
        RU_TO_EN_MAP.put('Ц', "Ts");
        RU_TO_EN_MAP.put('Ч', "Ch");
        RU_TO_EN_MAP.put('Ш', "Sh");
        RU_TO_EN_MAP.put('Щ', "Shch");
        RU_TO_EN_MAP.put('Ы', "Y");
        RU_TO_EN_MAP.put('Э', "E");
        RU_TO_EN_MAP.put('Ю', "Yu");
        RU_TO_EN_MAP.put('Я', "Ya");

        // Lowercase mapping
        RU_TO_EN_MAP.put('а', "a");
        RU_TO_EN_MAP.put('б', "b");
        RU_TO_EN_MAP.put('в', "v");
        RU_TO_EN_MAP.put('г', "g");
        RU_TO_EN_MAP.put('д', "d");
        RU_TO_EN_MAP.put('е', "e");
        RU_TO_EN_MAP.put('ё', "yo");
        RU_TO_EN_MAP.put('ж', "zh");
        RU_TO_EN_MAP.put('з', "z");
        RU_TO_EN_MAP.put('и', "i");
        RU_TO_EN_MAP.put('й', "y");
        RU_TO_EN_MAP.put('к', "k");
        RU_TO_EN_MAP.put('л', "l");
        RU_TO_EN_MAP.put('м', "m");
        RU_TO_EN_MAP.put('н', "n");
        RU_TO_EN_MAP.put('о', "o");
        RU_TO_EN_MAP.put('п', "p");
        RU_TO_EN_MAP.put('р', "r");
        RU_TO_EN_MAP.put('с', "s");
        RU_TO_EN_MAP.put('т', "t");
        RU_TO_EN_MAP.put('у', "u");
        RU_TO_EN_MAP.put('ф', "f");
        RU_TO_EN_MAP.put('х', "kh");
        RU_TO_EN_MAP.put('ц', "ts");
        RU_TO_EN_MAP.put('ч', "ch");
        RU_TO_EN_MAP.put('ш', "sh");
        RU_TO_EN_MAP.put('щ', "shch");
        RU_TO_EN_MAP.put('ы', "y");
        RU_TO_EN_MAP.put('э', "e");
        RU_TO_EN_MAP.put('ю', "yu");
        RU_TO_EN_MAP.put('я', "ya");
    }

    /**
     * Transliterates a Russian string to English and makes it URL-slug-friendly:
     *  - Replaces each Russian character with its English counterpart.
     *  - Removes characters that are not letters, digits, spaces, or dashes.
     *  - Replaces spaces with dashes.
     *  - Collapses multiple consecutive dashes into a single dash.
     *  - Converts to lowercase.
     */
    public static String transliterateRuToEn(String ruName) {
        // Step 1: Transliterate each character
        StringBuilder sb = new StringBuilder();
        for (char ch : ruName.toCharArray()) {
            sb.append(RU_TO_EN_MAP.getOrDefault(ch, String.valueOf(ch)));
        }

        // Step 2: Remove invalid characters (only keep letters, digits, spaces, dashes)
        String transliterated = sb.toString().replaceAll("[^a-zA-Z0-9\\-\\s]", "");

        // Step 3: Replace spaces with dashes
        transliterated = transliterated.replaceAll("\\s+", "-");

        // Step 4: Collapse multiple dashes into a single dash
        transliterated = transliterated.replaceAll("-{2,}", "-");

        // Step 5: Convert to lowercase
        transliterated = transliterated.toLowerCase();

        // Step 6: Remove leading/trailing dashes
        transliterated = transliterated.replaceAll("^-+|-+$", "");

        return transliterated;
    }

    /**
     * Transliterates a Ukrainian string to English and makes it URL-slug-friendly:
     *  - Replaces each Ukrainian character with its English counterpart.
     *  - Removes characters that are not letters, digits, spaces, or dashes.
     *  - Replaces spaces with dashes.
     *  - Collapses multiple consecutive dashes into a single dash.
     *  - Optionally trims leading/trailing dashes and converts to lowercase.
     */
    public static String transliterateUaToEn(String uaName) {
        // Step 1: Transliterate each character according to UA_TO_EN_MAP
        StringBuilder sb = new StringBuilder();
        for (char ch : uaName.toCharArray()) {
            sb.append(UA_TO_EN_MAP.getOrDefault(ch, String.valueOf(ch)));
        }

        // Step 2: Remove characters not allowed in a typical slug
        // (keep letters, digits, spaces, and dashes; adjust if needed)
        String transliterated = sb.toString();
        transliterated = transliterated.replaceAll("[^a-zA-Z0-9\\-\\s]", "");

        // Step 3: Replace one or more whitespace characters with a dash
        transliterated = transliterated.replaceAll("\\s+", "-");

        // Step 4: Collapse multiple consecutive dashes into a single dash
        transliterated = transliterated.replaceAll("-{2,}", "-");

        // (Optional) Step 5: Convert to lowercase if you want a lowercase slug
        transliterated = transliterated.toLowerCase();

        // (Optional) Step 6: Remove leading and trailing dashes
        transliterated = transliterated.replaceAll("^-+|-+$", "");

        return transliterated;
    }

    /**
     * Returns an English-transliterated, URL-slug-friendly version of uaName
     * if alias is numeric; otherwise, returns the original alias.
     */
    public static String getTransliteratedAlias(String alias, String uaName) {
        // Check if alias consists only of digits
        if (alias == null || alias.matches("\\d+") || alias.isEmpty()) {
            // Return a slug-friendly transliteration of uaName
            return transliterateUaToEn(uaName);
        } else {
            // If alias is not only digits, return alias unchanged
            return alias;
        }
    }

}
