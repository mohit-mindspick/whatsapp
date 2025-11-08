package com.assetneuron.whatsapp.common.adaptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class for extracting Bearer token from HTTP request
 */
@Component
@Slf4j
public class RequestTokenUtil {

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
}

