package com.codefactory.reservasmscatalogservice.security.impl;

import com.codefactory.reservasmscatalogservice.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, String userEmail) {
        final String username = extractUsername(token);
        return username != null && username.equals(userEmail) && !isTokenExpired(token);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);
    /*@Override
    public Claims extractAllClaims(String token) {
        
        try {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    } catch (Exception e) {
        logger.error("Error parsing JWT: {}", e.getMessage());
        throw e;
    }
        /*return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        */
    
    @Override
public Claims extractAllClaims(String token) {
    logger.debug("JWT Secret (first 10 chars): {}", secretKey != null ? secretKey.substring(0, 10) : "NULL");
    // Log the token header
    String[] parts = token.split("\\.");
    if (parts.length > 0) {
        String header = new String(java.util.Base64.getDecoder().decode(parts[0]));
        logger.debug("JWT Header: {}", header);
    }
    try {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    } catch (Exception e) {
        logger.error("Error parsing JWT: {}", e.getMessage());
        throw e;
    }
}

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}