package com.ua.buybooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BuyBooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyBooksApplication.class, args);
    }

}
