package com.codefactory.reservasmscatalogservice.config;

import com.codefactory.reservasmscatalogservice.security.JwtAccessDeniedHandler;
import com.codefactory.reservasmscatalogservice.security.JwtAuthenticationEntryPoint;
import com.codefactory.reservasmscatalogservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> null))
                .authorizeHttpRequests(auth -> auth
                        // Health check público
                        .requestMatchers("/api/", "/api/version").permitAll()
                        // Swagger/OpenAPI - Documentación pública
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/configuration/**").permitAll()
                        // Category endpoints - GET públicos (cualquiera puede ver categorías activas)
                        .requestMatchers(HttpMethod.GET, "/api/catalog/categories/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalog/categories/{id}").permitAll()
                        // Category endpoint - todas las categorías (incluyendo inactivas) solo para admins
                        .requestMatchers(HttpMethod.GET, "/api/catalog/categories").hasRole("ADMIN")
                        // Category endpoints de escritura - solo admin
                        .requestMatchers(HttpMethod.POST, "/api/catalog/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/categories/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/categories/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/catalog/categories/{id}/activate").hasRole("ADMIN")
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}