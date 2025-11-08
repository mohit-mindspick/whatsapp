package com.assetneuron.whatsapp.config;

import com.assetneuron.whatsapp.common.util.CorrelationIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to handle correlation IDs and API versioning
 */
@Component
@Slf4j
public class ApiConfiguration implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Set correlation ID
        String correlationId = CorrelationIdUtil.getOrGenerateCorrelationId(request);
        CorrelationIdUtil.setCorrelationId(correlationId);
        response.setHeader("X-Correlation-ID", correlationId);
        
        // Set API version header
        response.setHeader("X-API-Version", "v1");
        
        log.debug("Request: {} {} - Correlation ID: {}", 
                request.getMethod(), request.getRequestURI(), correlationId);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
        // Clear correlation ID from MDC
        CorrelationIdUtil.clearCorrelationId();
    }
}

