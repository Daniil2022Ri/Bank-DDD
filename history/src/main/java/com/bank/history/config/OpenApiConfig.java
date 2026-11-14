package com.bank.history.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "History service API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "Микросервис истории изменений и агрегации аудита";

    @Bean
    public OpenAPI historyOpenApi() {
        final Info info = new Info().title(API_TITLE).version(API_VERSION).description(API_DESCRIPTION);
        return new OpenAPI().info(info);
    }
}
