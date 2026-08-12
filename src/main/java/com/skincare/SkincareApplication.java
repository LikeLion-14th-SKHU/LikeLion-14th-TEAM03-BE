package com.skincare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkinCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkinCareApplication.class, args);
    }
}