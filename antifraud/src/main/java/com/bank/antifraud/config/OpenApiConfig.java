package com.bank.antifraud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "Anti-fraud service API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "Микросервис выявления и блокировки подозрительных переводов";

    @Bean
    public OpenAPI antifraudOpenApi() {
        final Info info = new Info().title(API_TITLE).version(API_VERSION).description(API_DESCRIPTION);
        return new OpenAPI().info(info);
    }
}
