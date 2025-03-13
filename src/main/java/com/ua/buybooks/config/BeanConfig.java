package com.ua.buybooks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;

@Configuration
public class BeanConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}
