package com.bank.publicinfo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "Public info service API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "Публичная информация о банке, отделениях, банкоматах и лицензиях";

    @Bean
    public OpenAPI publicInfoOpenApi() {
        final Info info = new Info().title(API_TITLE).version(API_VERSION).description(API_DESCRIPTION);
        return new OpenAPI().info(info);
    }
}
