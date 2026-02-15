package com.assetneuron.whatsapp.common.security;

import com.assetneuron.whatsapp.common.model.TenantSettings;
import com.assetneuron.whatsapp.common.model.UserSession;
import com.assetneuron.whatsapp.common.service.TenantSettingsService;
import com.assetneuron.whatsapp.common.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserSessionService userSessionService;
    private final TenantSettingsService tenantSettingsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {

        // Create a wrapper to add headers if needed
        TenantHeaderRequestWrapper requestWrapper = new TenantHeaderRequestWrapper(request);

        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtUtil.isTokenValid(token)) {
                    // Extract claims from JWT
                    io.jsonwebtoken.Claims claims = jwtUtil.extractAllClaims(token);
                    String username = claims.getSubject();

                    // Extract tenant ID from JWT first to check multiDeviceEnabled
                    String tenantId = jwtUtil.extractTenantId(token);
                    
                    // Check if multi-device is enabled for this tenant
                    boolean multiDeviceEnabled = false;
                    if (tenantId != null && !tenantId.isEmpty()) {
                        try {
                            UUID tenantUuid = UUID.fromString(tenantId);
                            TenantSettings tenantSettings = tenantSettingsService.getTenantSettingsByTenantId(tenantUuid);
                            if (tenantSettings != null) {
                                multiDeviceEnabled = tenantSettings.getMultiDeviceEnabled() != null && tenantSettings.getMultiDeviceEnabled();
                            }
                        } catch (Exception e) {
                            log.warn("Could not retrieve tenant settings for tenant: {}, defaulting to single device mode", tenantId, e);
                        }
                    }

                    // Extract and validate session ID from JWT (skip if multiDeviceEnabled is true)
                    if (!multiDeviceEnabled) {
                        String sessionIdStr = jwtUtil.extractSessionId(token);
                        if (sessionIdStr != null && !sessionIdStr.isEmpty()) {
                            try {
                                UUID sessionId = UUID.fromString(sessionIdStr);
                                // Check if session exists and is active
                                try {
                                    UserSession userSession = userSessionService.getUserSessionBySessionId(sessionId);
                                    if (!userSession.getActive()) {
                                        log.warn("Session is not active for sessionId: {}", sessionId);
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                        response.setContentType("application/json");
                                        response.getWriter().write("{\"error\":\"ERR_SESSION_NOT_FOUND\"}");
                                        return;
                                    }
                                    log.debug("Session validated successfully for sessionId: {}", sessionId);
                                } catch (RuntimeException e) {
                                    if (e.getMessage() != null && e.getMessage().equals("ERR_SESSION_NOT_FOUND")) {
                                        log.warn("Session not found for sessionId: {}", sessionId);
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                        response.setContentType("application/json");
                                        response.getWriter().write("{\"error\":\"ERR_SESSION_NOT_FOUND\"}");
                                        return;
                                    }
                                    throw e;
                                }
                            } catch (IllegalArgumentException e) {
                                log.warn("Invalid sessionId format in token: {}", sessionIdStr);
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"ERR_SESSION_NOT_FOUND\"}");
                                return;
                            }
                        }
                    } else {
                        log.debug("Multi-device enabled for tenant: {}, skipping session validation", tenantId);
                    }
                    
                    // Validate/add tenant ID to headers
                    if (tenantId != null && !tenantId.isEmpty()) {
                        String existingTenantId = request.getHeader("X-Tenant-Id");
                        if (existingTenantId != null && !existingTenantId.isEmpty()) {
                            // Validate: If X-Tenant-Id is present, it must match the token's tenantId
                            if (!tenantId.equals(existingTenantId)) {
                                log.warn("Tenant ID mismatch: Header X-Tenant-Id={} does not match token tenantId={}", 
                                        existingTenantId, tenantId);
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.getWriter().write("{\"error\":\"Tenant ID mismatch\"}");
                                return;
                            }
                            log.debug("X-Tenant-Id header validated: {}", existingTenantId);
                        } else {
                            // If X-Tenant-Id is null, extract from token and set in header
                            requestWrapper.addHeader("X-Tenant-Id", tenantId);
                            log.debug("Added X-Tenant-Id header from JWT: {}", tenantId);
                        }
                    }

                    addAuthorizedSiteIdsHeader(token, request, requestWrapper);

                    // Extract roles and permissions from JWT claims
                    @SuppressWarnings("unchecked")
                    List<String> roles = claims.get("roles", List.class);
                    @SuppressWarnings("unchecked")
                    List<String> permissions = claims.get("permissions", List.class);
                    
                    // Create authorities from JWT claims
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    
                    if (roles != null) {
                        roles.forEach(role -> 
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                    
                    if (permissions != null) {
                        permissions.forEach(permission -> 
                            authorities.add(new SimpleGrantedAuthority(permission)));
                    }
                    
                    // Create authentication object
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT authentication successful for user: {} with tenant: {}", username, tenantId);
                } else {
                    log.warn("Invalid JWT token provided");
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            // Clear security context on error
            SecurityContextHolder.clearContext();
        }
        
        // Continue with the filter chain using the wrapper
        filterChain.doFilter(requestWrapper, response);
    }

    /**
     * Sets X-AUTHORIZED-SITE-IDS on the request: intersection of token siteIds and request X-SITE-ID (if present).
     * If X-SITE-ID is not present, uses all site IDs from the token (comma separated).
     */
    private void addAuthorizedSiteIdsHeader(String token, HttpServletRequest request, TenantHeaderRequestWrapper requestWrapper) {
        List<String> tokenSiteIds = jwtUtil.extractSiteIds(token);
        if (tokenSiteIds == null || tokenSiteIds.isEmpty()) {
            return;
        }
        String requestSiteIdsHeader = request.getHeader("X-SITE-ID");
        String authorizedSiteIdsValue;
        if (requestSiteIdsHeader == null || requestSiteIdsHeader.trim().isEmpty()) {
            authorizedSiteIdsValue = String.join(",", tokenSiteIds);
            log.debug("X-AUTHORIZED-SITE-IDS set from token (no request header): {}", authorizedSiteIdsValue);
        } else {
            Set<String> tokenSiteSet = tokenSiteIds.stream().map(String::trim).collect(Collectors.toSet());
            List<String> requestSiteIds = Arrays.stream(requestSiteIdsHeader.split(","))
                    .map(String::trim)
                    .filter(id -> !id.isEmpty())
                    .collect(Collectors.toList());
            List<String> intersection = requestSiteIds.stream()
                    .filter(tokenSiteSet::contains)
                    .collect(Collectors.toList());
            authorizedSiteIdsValue = String.join(",", intersection);
            log.debug("X-AUTHORIZED-SITE-IDS set as intersection of token and request X-SITE-ID: {}", authorizedSiteIdsValue);
        }
        requestWrapper.addHeader("X-AUTHORIZED-SITE-IDS", authorizedSiteIdsValue);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT filter for public endpoints
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/health") ||
               path.startsWith("/api/auth/test") ||
               path.startsWith("/api/events") ||
               path.startsWith("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/api/v1/whatsapp/health") ||
               path.startsWith("/whatsapp/health");
    }
}

