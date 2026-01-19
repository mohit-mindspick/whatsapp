package com.assetneuron.whatsapp.common.filter;

import com.assetneuron.whatsapp.common.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * Filter to enforce geofencing restrictions based on user's assigned sites.
 * This filter checks if the request's latitude/longitude are within the geofence
 * boundaries of any of the user's assigned sites.
 * 
 * Filter flow:
 * 1. Extracts JWT token from Authorization header
 * 2. Extracts "sites" claim from JWT token (contains geofencing information)
 * 3. If sites claim exists in token:
 *    - Requires coordinates from HTTP headers:
 *      - x-latitude or X-Latitude: Latitude coordinate (required if sites claim exists)
 *      - x-longitude or X-Longitude: Longitude coordinate (required if sites claim exists)
 *    - Validates coordinates against sites from JWT token using geofence radius
 * 4. If sites claim does not exist, skips geofencing validation and proceeds with request
 * 
 * Note: The sites claim is only present in the token when:
 * - Tenant has geofencing enabled (TenantSettings.geofencingEnabled = true)
 * - User has at least one role with geofencing enabled (Role.geofencingEnabled = true)
 * - User has assigned sites with geofencing configured
 * 
 * If sites claim exists but coordinates are missing, returns 400 BAD_REQUEST error.
 * If coordinates are outside all site geofences, returns 403 FORBIDDEN error.
 */
@Component
@Order(org.springframework.core.Ordered.LOWEST_PRECEDENCE - 10) // Run after JWT filter but before controllers
@RequiredArgsConstructor
@Slf4j
public class GeofencingFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    
    private static final double EARTH_RADIUS_METRES = 6371000; // Earth's radius in metres
    private static final String ERR_GEOFENCE_COORDINATES_MISSING = "ERR_GEOFENCE_COORDINATES_MISSING";
    private static final String ERR_GEOFENCE_VIOLATION = "ERR_GEOFENCE_VIOLATION";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // Skip geofencing check for unauthenticated requests
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            log.debug("Skipping geofencing check for unauthenticated request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from Authorization header
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.isTokenValid(token)) {
                log.debug("No valid JWT token found, skipping geofencing check: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // Extract sites claim from JWT token
            // If sites claim exists, geofencing is enabled (it's only added when tenant and user role both have geofencing enabled)
            Claims claims = jwtUtil.extractAllClaims(token);
            List<Map<String, Object>> sitesClaim = extractSitesClaim(claims);
            
            // If no sites claim in token, geofencing is not enabled - skip validation and proceed
            if (sitesClaim == null || sitesClaim.isEmpty()) {
                log.debug("No sites claim in JWT token - geofencing is not enabled, proceeding with request");
                filterChain.doFilter(request, response);
                return;
            }

            // Sites claim exists - coordinates are now REQUIRED
            // Extract latitude and longitude from headers
            Double latitude = extractLatitude(request);
            Double longitude = extractLongitude(request);

            // If coordinates are not provided and sites claim exists, throw error
            if (latitude == null || longitude == null) {
                log.warn("Sites claim found in token but latitude/longitude not provided in headers. Request URI: {}", 
                         request.getRequestURI());
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                String errorResponse = String.format(
                    "{\"error\":\"%s\",\"message\":\"User has role with geofencing enabled. x-latitude/X-Latitude and x-longitude/X-Longitude headers are required\"}",
                    ERR_GEOFENCE_COORDINATES_MISSING);
                response.getWriter().write(errorResponse);
                return;
            }

            // Check if coordinates are within any site's geofence from JWT token
            boolean withinGeofence = isWithinGeofenceFromClaims(latitude, longitude, sitesClaim);
            
            String username = authentication.getName();
            
            if (!withinGeofence) {
                log.warn("Geofence violation: User {} attempted to access from coordinates ({}, {}) " +
                         "which are outside all assigned site boundaries. Request URI: {}", 
                         username, latitude, longitude, request.getRequestURI());
                
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                String errorResponse = String.format(
                    "{\"error\":\"%s\",\"message\":\"Access denied: Location (%.6f, %.6f) is outside geofence boundaries\"}",
                    ERR_GEOFENCE_VIOLATION, latitude, longitude);
                response.getWriter().write(errorResponse);
                return;
            }

            log.debug("Geofencing check passed for user {} at coordinates ({}, {}) for request: {}", 
                     username, latitude, longitude, request.getRequestURI());

        } catch (Exception e) {
            log.error("Error during geofencing check for request: {}", request.getRequestURI(), e);
            // On error, allow request to proceed (fail-open) - adjust based on your security requirements
            // Alternatively, you could fail closed by throwing an exception or returning 403
            log.warn("Allowing request to proceed due to geofencing check error (fail-open behavior)");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     * 
     * @param request HTTP request
     * @return JWT token string or null if not present
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Extract sites claim from JWT token claims.
     * 
     * @param claims JWT claims
     * @return List of site maps with geofencing information, or null if not present
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractSitesClaim(Claims claims) {
        if (claims == null) {
            return null;
        }
        
        try {
            Object sitesObj = claims.get("sites");
            if (sitesObj == null) {
                return null;
            }
            
            // Handle case where sites is already a List<Map>
            if (sitesObj instanceof List) {
                List<?> sitesList = (List<?>) sitesObj;
                List<Map<String, Object>> sites = new ArrayList<>();
                
                for (Object siteObj : sitesList) {
                    if (siteObj instanceof Map) {
                        Map<String, Object> siteMap = (Map<String, Object>) siteObj;
                        // Validate that site has required fields
                        if (siteMap.containsKey("latitude") && siteMap.containsKey("longitude") 
                            && siteMap.containsKey("geofenceRadiusMetres")) {
                            sites.add(siteMap);
                        }
                    }
                }
                
                return sites.isEmpty() ? null : sites;
            }
            
            return null;
        } catch (Exception e) {
            log.warn("Error extracting sites claim from JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if coordinates are within geofence boundaries using sites from JWT token claims.
     * 
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param sitesClaim List of site maps from JWT token
     * @return true if coordinates are within any site's geofence, false otherwise
     */
    private boolean isWithinGeofenceFromClaims(double latitude, double longitude, List<Map<String, Object>> sitesClaim) {
        if (sitesClaim == null || sitesClaim.isEmpty()) {
            log.debug("No sites in claim for geofencing validation");
            return false;
        }

        for (Map<String, Object> site : sitesClaim) {
            try {
                Object latObj = site.get("latitude");
                Object lonObj = site.get("longitude");
                Object radiusObj = site.get("geofenceRadiusMetres");

                if (latObj == null || lonObj == null || radiusObj == null) {
                    log.debug("Site missing required geofencing fields, skipping");
                    continue;
                }

                Double siteLatitude = convertToDouble(latObj);
                Double siteLongitude = convertToDouble(lonObj);
                Double radiusMetres = convertToDouble(radiusObj);

                if (siteLatitude == null || siteLongitude == null || radiusMetres == null || radiusMetres <= 0) {
                    log.debug("Invalid geofencing values for site, skipping");
                    continue;
                }

                // Calculate distance using Haversine formula
                double distance = calculateDistance(latitude, longitude, siteLatitude, siteLongitude);

                if (distance <= radiusMetres) {
                    log.debug("Coordinates ({}, {}) are within geofence of site (lat: {}, lon: {}, radius: {} m, distance: {} m)",
                             latitude, longitude, siteLatitude, siteLongitude, radiusMetres, distance);
                    return true;
                }

                log.debug("Coordinates ({}, {}) are {} m away from site (lat: {}, lon: {}, radius: {} m)",
                         latitude, longitude, distance, siteLatitude, siteLongitude, radiusMetres);
            } catch (Exception e) {
                log.warn("Error checking geofence for site: {}", e.getMessage());
                continue;
            }
        }

        log.debug("Coordinates ({}, {}) are not within any site's geofence", latitude, longitude);
        return false;
    }

    /**
     * Calculate the distance between two points on Earth using Haversine formula.
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in metres
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if (!isValidCoordinate(lat1, lon1) || !isValidCoordinate(lat2, lon2)) {
            log.warn("Invalid coordinates provided: ({}, {}) or ({}, {})", lat1, lon1, lat2, lon2);
            throw new IllegalArgumentException("Invalid coordinates provided");
        }

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        double distance = EARTH_RADIUS_METRES * c;
        log.debug("Distance between ({}, {}) and ({}, {}): {} metres", lat1, lon1, lat2, lon2, distance);
        
        return distance;
    }

    /**
     * Validate if coordinates are valid (latitude: -90 to 90, longitude: -180 to 180)
     * 
     * @param latitude Latitude to validate
     * @param longitude Longitude to validate
     * @return true if coordinates are valid, false otherwise
     */
    private boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    /**
     * Convert object to Double, handling various number types.
     * 
     * @param obj Object to convert
     * @return Double value or null if conversion fails
     */
    private Double convertToDouble(Object obj) {
        if (obj == null) {
            return null;
        }
        
        if (obj instanceof Double) {
            return (Double) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException e) {
                log.warn("Could not parse string to double: {}", obj);
                return null;
            }
        }
        
        log.warn("Cannot convert object to double: {} (type: {})", obj, obj.getClass().getName());
        return null;
    }

    /**
     * Extract latitude from x-latitude or X-Latitude header.
     * Checks lowercase first, then camel case.
     * 
     * @param request HTTP request
     * @return Latitude value or null if not present/invalid
     */
    private Double extractLatitude(HttpServletRequest request) {
        String latHeader = request.getHeader("x-latitude");
        if (latHeader == null || latHeader.isEmpty()) {
            latHeader = request.getHeader("X-Latitude");
        }
        if (latHeader == null || latHeader.isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(latHeader.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid latitude format in latitude header: {}", latHeader);
            return null;
        }
    }

    /**
     * Extract longitude from x-longitude or X-Longitude header.
     * Checks lowercase first, then camel case.
     * 
     * @param request HTTP request
     * @return Longitude value or null if not present/invalid
     */
    private Double extractLongitude(HttpServletRequest request) {
        String lonHeader = request.getHeader("x-longitude");
        if (lonHeader == null || lonHeader.isEmpty()) {
            lonHeader = request.getHeader("X-Longitude");
        }
        if (lonHeader == null || lonHeader.isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(lonHeader.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid longitude format in longitude header: {}", lonHeader);
            return null;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip geofencing for public endpoints
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
