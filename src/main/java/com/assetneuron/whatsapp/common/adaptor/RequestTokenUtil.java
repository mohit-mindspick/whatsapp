package com.assetneuron.whatsapp.common.adaptor;

import com.assetneuron.whatsapp.common.exception.InvalidAuthTokenException;
import com.assetneuron.whatsapp.common.exception.MissingAuthTokenException;
import com.assetneuron.whatsapp.common.exception.TenantIdNotFoundException;
import com.assetneuron.whatsapp.common.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Utility class for extracting Bearer token from HTTP request
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestTokenUtil {

    private final JwtUtil jwtUtil;

    /**
     * Get Bearer token from current HTTP request
     *
     * @return Bearer token with "Bearer " prefix, or null if not found
     */
    public String getBearerTokenFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader; // Return with "Bearer " prefix
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract bearer token from request: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Get X-Tenant-Id from current HTTP request header
     *
     * @return Tenant ID as string, or null if not found
     */
    public String getTenantIdFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("X-Tenant-Id");
            }
        } catch (Exception e) {
            log.debug("Could not extract tenant ID from request: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Extract tenant ID from JWT token in the current HTTP request
     * Similar to how JwtAuthenticationFilter extracts tenant ID
     *
     * @return Tenant ID as UUID
     * @throws MissingAuthTokenException if Authorization header is missing or doesn't start with "Bearer "
     * @throws InvalidAuthTokenException if the token is invalid or expired
     * @throws TenantIdNotFoundException if tenant ID is not found in the token
     */
    public UUID getTenantIdFromToken() {
        String authHeader = getBearerTokenFromRequest();
        
        // Check if Authorization header is missing
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authentication token is missing from request");
            throw new MissingAuthTokenException("Authentication token is missing. Please provide a valid Bearer token in the Authorization header.");
        }
        
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // Validate token
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("Invalid or expired authentication token provided");
                throw new InvalidAuthTokenException("Invalid or expired authentication token. Please provide a valid token.");
            }
            
            // Extract tenant ID from JWT
            String tenantId = jwtUtil.extractTenantId(token);
            if (tenantId == null || tenantId.isEmpty()) {
                log.warn("Tenant ID not found in authentication token");
                throw new TenantIdNotFoundException("Tenant ID not found in authentication token. Please ensure your token contains a valid tenant ID.");
            }
            
            try {
                UUID tenantUuid = UUID.fromString(tenantId);
                log.debug("Extracted tenant ID from JWT token: {}", tenantUuid);
                return tenantUuid;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid tenant ID format in token: {}", tenantId);
                throw new TenantIdNotFoundException("Invalid tenant ID format in authentication token: " + tenantId);
            }
            
        } catch (MissingAuthTokenException | InvalidAuthTokenException | TenantIdNotFoundException e) {
            // Re-throw our custom exceptions
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error extracting tenant ID from JWT token: {}", e.getMessage(), e);
            throw new InvalidAuthTokenException("Failed to extract tenant ID from authentication token: " + e.getMessage(), e);
        }
    }
}

