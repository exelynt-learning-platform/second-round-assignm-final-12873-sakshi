package com.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new RuntimeException("JWT secret key must be at least 32 characters long");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ GENERATE TOKEN
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // ✅ EXTRACT USERNAME
    public String extractUsername(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            return null; // 🔥 Prevent crash
        }
    }

    // ✅ EXTRACT CLAIMS
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(String token, String username) {
        try {
            return username != null &&
                    username.equals(extractUsername(token)) &&
                    !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ⏰ CHECK EXPIRY
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}