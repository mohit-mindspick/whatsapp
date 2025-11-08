package com.assetneuron.whatsapp.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utility class for managing correlation IDs across the application.
 * Provides methods to generate, set, get, and clear correlation IDs.
 */
@Slf4j
public class CorrelationIdUtil {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    /**
     * Generates a new correlation ID
     * @return a new UUID-based correlation ID
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Sets the correlation ID in the current thread's MDC context
     * @param correlationId the correlation ID to set
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.trim().isEmpty()) {
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            log.debug("Correlation ID set: {}", correlationId);
        }
    }

    /**
     * Gets the correlation ID from the current thread's MDC context
     * @return the correlation ID or null if not set
     */
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID_MDC_KEY);
    }

    /**
     * Clears the correlation ID from the current thread's MDC context
     */
    public static void clearCorrelationId() {
        String correlationId = getCorrelationId();
        MDC.remove(CORRELATION_ID_MDC_KEY);
        if (correlationId != null) {
            log.debug("Correlation ID cleared: {}", correlationId);
        }
    }

    /**
     * Extracts correlation ID from HTTP request header
     * @param request the HTTP request
     * @return the correlation ID from header or null if not present
     */
    public static String getCorrelationIdFromRequest(HttpServletRequest request) {
        return request.getHeader(CORRELATION_ID_HEADER);
    }

    /**
     * Gets correlation ID from request header or generates a new one if not present
     * @param request the HTTP request
     * @return the correlation ID from header or a newly generated one
     */
    public static String getOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = getCorrelationIdFromRequest(request);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = generateCorrelationId();
            log.debug("Generated new correlation ID: {}", correlationId);
        }
        return correlationId;
    }
}

