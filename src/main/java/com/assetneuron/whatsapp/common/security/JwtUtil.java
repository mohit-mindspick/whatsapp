package com.assetneuron.whatsapp.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expirationMs:86400000}")
    private long expirationMs;
    
    @Value("${jwt.refreshExpirationMs:604800000}")
    private long refreshExpirationMs; // 7 days default
    
    private SecretKey key;

    public JwtUtil() {
        // Default constructor
    }

    private SecretKey getKey() {
        if (key == null) {
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        }
        return key;
    }

    public String generateToken(String username, String email, UUID tenantId, List<String> roles, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("email", email);
        claims.put("tenantId", tenantId != null ? tenantId.toString() : null);
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            log.debug("JWT Token Validation Failed. Exception: {}", e);
            return false;
        }
    }
    
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }
    
    public String extractTenantId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("tenantId", String.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("permissions", List.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Generate a refresh token for a user
     * @param userId the user's ID
     * @param username the username
     * @return the generated refresh token
     */
    public String generateRefreshToken(UUID userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("type", "refresh");
        
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }
    
    /**
     * Validate if a token is a refresh token
     * @param token the token to validate
     * @return true if it's a valid refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract user ID from token
     * @param token the token
     * @return the user ID
     */
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }
}

