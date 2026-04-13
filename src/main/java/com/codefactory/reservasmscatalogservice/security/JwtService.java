package com.codefactory.reservasmscatalogservice.security;

import io.jsonwebtoken.Claims;
import java.util.function.Function;

public interface JwtService {

    // Solo métodos de validación/extracción (NO generación)
    String extractUsername(String token);
    boolean isTokenValid(String token, String userEmail);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    Claims extractAllClaims(String token);
}