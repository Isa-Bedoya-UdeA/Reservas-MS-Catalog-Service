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
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final JwtAuthenticationEntryPoint authenticationEntryPoint;
        private final JwtAccessDeniedHandler accessDeniedHandler;
        private final CorsConfigurationSource corsConfigurationSource;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/", "/api/version").permitAll()
                                                .requestMatchers("/swagger-ui.html").permitAll()
                                                .requestMatchers("/swagger-ui/**").permitAll()
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                .requestMatchers("/swagger-resources/**").permitAll()
                                                .requestMatchers("/webjars/**").permitAll()
                                                .requestMatchers("/configuration/**").permitAll()
                                                // Actuator endpoints para Prometheus (no exponer en prod)
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/catalog/categories/active")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/catalog/categories/{id}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/catalog/categories")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/catalog/categories")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/catalog/categories/{id}")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/catalog/categories/{id}")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.PATCH,
                                                                "/api/catalog/categories/{id}/activate")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/catalog/services/active")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/catalog/services/active/category/{idCategoria}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/catalog/services/active/provider/{idProveedor}")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/catalog/services").hasAuthority("PROVEEDOR")
                                                .requestMatchers(HttpMethod.PUT, "/api/catalog/services/{id}")
                                                .hasAuthority("PROVEEDOR")
                                                .requestMatchers(HttpMethod.DELETE, "/api/catalog/services/{id}")
                                                .hasAuthority("PROVEEDOR")
                                                .requestMatchers(HttpMethod.PATCH, "/api/catalog/services/{id}/disable")
                                                .hasAuthority("PROVEEDOR")
                                                .requestMatchers(HttpMethod.GET, "/api/catalog/services/provider")
                                                .hasAuthority("PROVEEDOR")
                                                .requestMatchers(HttpMethod.DELETE,
                                                                "/api/catalog/services/{id}/permanent")
                                                .hasAuthority("ADMIN")
                                                .anyRequest().authenticated())
                                .exceptionHandling(e -> e
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}