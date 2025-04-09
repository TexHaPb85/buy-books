package com.ua.buybooks.util.constants;

public enum CountryCode {
    UA("uk"),
    RU("ru");

    private final String code;

    CountryCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
