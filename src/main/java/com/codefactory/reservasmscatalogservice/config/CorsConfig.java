package com.codefactory.reservasmscatalogservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
                frontendUrl,
                "http://localhost:3000",
                "http://localhost:5173"
        ));
        config.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"
        ));
        config.setExposedHeaders(List.of("Authorization"));
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}