package com.assetneuron.whatsapp.common.security;

import com.assetneuron.whatsapp.common.security.service.RolePermissionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RolePermissionService rolePermissionService;

    @Value("${authorization.skip:false}")
    private boolean authorizationSkip;

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

                    // Extract tenant ID from JWT and validate/add to headers
                    String tenantId = jwtUtil.extractTenantId(token);
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

                    // Extract roles from JWT claims
                    @SuppressWarnings("unchecked")
                    List<String> roles = claims.get("roles", List.class);
                    
                    // Create authorities collection
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    
                    // Add role authorities
                    if (roles != null && !roles.isEmpty()) {
                        roles.forEach(role -> 
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                    
                    // Check if authorization skip is enabled
                    if (authorizationSkip) {
                        // Skip authorization check - add all available permissions in the system
                        log.debug("Authorization skip is enabled - adding all system permissions");
                        Set<String> allPermissions = rolePermissionService.getAllPermissions();
                        if (allPermissions != null && !allPermissions.isEmpty()) {
                            allPermissions.forEach(permission -> 
                                authorities.add(new SimpleGrantedAuthority(permission)));
                            log.debug("Added {} system permissions to authorities", allPermissions.size());
                        }
                    } else {
                        // Normal flow: fetch permissions mapped to roles from database
                        if (roles != null && !roles.isEmpty()) {
                            Set<String> permissionsFromRoles = rolePermissionService.getPermissionsForRoles(roles, tenantId);
                            
                            if (permissionsFromRoles != null && !permissionsFromRoles.isEmpty()) {
                                log.debug("Fetched {} permissions from database for roles: {}", 
                                        permissionsFromRoles.size(), roles);
                                permissionsFromRoles.forEach(permission -> 
                                    authorities.add(new SimpleGrantedAuthority(permission)));
                            } else {
                                log.debug("No permissions found for roles: {}", roles);
                            }
                        }
                        
                        // Also include any permissions directly in JWT (for backward compatibility)
                        @SuppressWarnings("unchecked")
                        List<String> jwtPermissions = claims.get("permissions", List.class);
                        if (jwtPermissions != null && !jwtPermissions.isEmpty()) {
                            jwtPermissions.forEach(permission -> 
                                authorities.add(new SimpleGrantedAuthority(permission)));
                        }
                    }
                    
                    // Create authentication object
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT authentication successful for user: {} with tenant: {}", username, tenantId);
                } else {
                    log.debug("Invalid JWT token provided");
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT filter for public endpoints - whatsapp specific paths
        return path.startsWith("/api/v1/events") ||
               path.startsWith("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/api/v1/whatsapp/health");
    }
}

