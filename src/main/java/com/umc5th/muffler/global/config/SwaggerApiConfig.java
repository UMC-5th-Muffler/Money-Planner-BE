package com.umc5th.muffler.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerApiConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("muffler API")
                .description("muffler의 API 문서입니다.");

        String key = "Access Token (Bearer)";
        String refreshKey = "Refresh Token";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(key)
                .addList(refreshKey);

        SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("Authorization")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        SecurityScheme refreshTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("AuthorizationType.REFRESH_TOKEN.getCode()");

        Components components = new Components()
                .addSecuritySchemes(key, accessTokenSecurityScheme)
                .addSecuritySchemes(refreshKey, refreshTokenSecurityScheme);
        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components)
                .info(info);
    }
}
