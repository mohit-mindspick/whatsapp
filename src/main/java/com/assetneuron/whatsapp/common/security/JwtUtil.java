package com.assetneuron.whatsapp.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expirationMs:86400000}")
    private long expirationMs;
    
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

    public String generateToken(String username, List<String> roles, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
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
            log.warn("JWT Token Validation Failed. Exception: {}", e);
            return false;
        }
    }
    
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
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

    public String extractTenantId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("tenantId", String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Extract session ID from token
     * @param token the token
     * @return the session ID, or null if not present
     */
    public String extractSessionId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("sessionId", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract site IDs from token (authorized sites for the user under the tenant).
     * @param token the token
     * @return list of site IDs, or null/empty if not present
     */
    @SuppressWarnings("unchecked")
    public List<String> extractSiteIds(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object siteIdsObj = claims.get("siteIds");
            if (siteIdsObj instanceof List) {
                List<?> list = (List<?>) siteIdsObj;
                List<String> result = new ArrayList<>();
                for (Object item : list) {
                    if (item != null) {
                        result.add(item.toString().trim());
                    }
                }
                return result.isEmpty() ? null : result;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}

