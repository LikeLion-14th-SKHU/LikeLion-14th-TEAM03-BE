package com.skincare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Skin Care API")
                        .description("Skin Care 서비스 API 명세서")
                        .version("v0.0.1"))
                .servers(List.of(
                        new Server().url("https://api.d-dayskincare.cloud")
                                .description("운영 서버"),
                        new Server().url("http://localhost:8080")
                                .description("로컬 서버")
                ));
    }
}