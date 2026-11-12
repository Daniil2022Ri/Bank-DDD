package com.bank.authorization.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "Authorization service API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "Микросервис управления пользователями и JWT-аутентификации";
    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI authorizationOpenApi() {
        final Info info = new Info().title(API_TITLE).version(API_VERSION).description(API_DESCRIPTION);
        final SecurityScheme scheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        final Components components = new Components().addSecuritySchemes(SECURITY_SCHEME, scheme);
        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
    }
}
