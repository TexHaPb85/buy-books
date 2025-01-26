package com.ua.buybooks.util;

import java.util.HashMap;
import java.util.Map;

public class TransliterationUtil {

    // A simple mapping from Ukrainian letters to their English transliteration
    private static final Map<Character, String> UA_TO_EN_MAP = new HashMap<>();

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

    /**
     * Transliterates a Ukrainian string to English and makes it URL-slug-friendly:
     *  - Replaces each Ukrainian character with its English counterpart.
     *  - Removes characters that are not letters, digits, spaces, or dashes.
     *  - Replaces spaces with dashes.
     *  - Collapses multiple consecutive dashes into a single dash.
     *  - Optionally trims leading/trailing dashes and converts to lowercase.
     */
    private static String transliterateUaToEn(String uaName) {
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

}
