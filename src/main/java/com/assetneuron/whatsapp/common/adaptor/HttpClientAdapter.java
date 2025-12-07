package com.assetneuron.whatsapp.common.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP Client Adapter for making REST API calls to external services
 * Supports Bearer token authentication and handles response extraction
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HttpClientAdapter {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RequestTokenUtil requestTokenUtil;

    /**
     * Execute GET request
     *
     * @param url         The service URL
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> get(String url, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();
        return executeRequest(HttpMethod.GET, url, bearerToken, null, null, null, responseType);
    }

    /**
     * Execute GET request with query parameters
     *
     * @param url         The service URL
     * @param queryParams Query parameters as a map
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> get(String url, Map<String, String> queryParams, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();
        return executeRequest(HttpMethod.GET, url, bearerToken, null, queryParams, null, responseType);
    }

    /**
     * Execute POST request
     *
     * @param url         The service URL
     * @param payload     Request payload object
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> post(String url, Object payload, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();

        log.info("bearerToken ---- {}",bearerToken);

        return executeRequest(HttpMethod.POST, url, bearerToken, payload, null, null, responseType);
    }

    /**
     * Execute POST request with custom headers
     *
     * @param url         The service URL
     * @param payload     Request payload object
     * @param customHeaders Custom headers to include in the request
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> post(String url, Object payload, Map<String, String> customHeaders, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();
        return executeRequest(HttpMethod.POST, url, bearerToken, payload, null, customHeaders, responseType);
    }

    /**
     * Execute PUT request
     *
     * @param url         The service URL
     * @param payload     Request payload object
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> put(String url, Object payload, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();
        return executeRequest(HttpMethod.PUT, url, bearerToken, payload, null, null, responseType);
    }

    /**
     * Execute PUT request with query parameters
     *
     * @param url         The service URL
     * @param queryParams Query parameters as a map
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> put(String url, Map<String, String> queryParams, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();
        return executeRequest(HttpMethod.PUT, url, bearerToken, null, queryParams, null, responseType);
    }

    /**
     * Execute DELETE request
     *
     * @param url         The service URL
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> delete(String url, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();
        return executeRequest(HttpMethod.DELETE, url, bearerToken, null, null, null, responseType);
    }

    /**
     * Execute PATCH request
     *
     * @param url         The service URL
     * @param payload     Request payload object
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    public <T> HttpClientResponse<T> patch(String url, Object payload, Class<T> responseType) {
        String bearerToken = requestTokenUtil.getBearerTokenFromRequest();
        return executeRequest(HttpMethod.PATCH, url, bearerToken, payload, null, null, responseType);
    }

    /**
     * Generic method to execute HTTP requests
     *
     * @param method      HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param url         The service URL
     * @param bearerToken Bearer token for authentication
     * @param payload     Request payload (can be null for GET/DELETE)
     * @param queryParams Query parameters (can be null)
     * @param customHeaders Custom headers to include (can be null)
     * @param responseType Class type for response body deserialization
     * @return HttpClientResponse containing status code and response body
     */
    private <T> HttpClientResponse<T> executeRequest(
            HttpMethod method,
            String url,
            String bearerToken,
            Object payload,
            Map<String, String> queryParams,
            Map<String, String> customHeaders,
            Class<T> responseType) {

        try {
            // Build URL with query parameters if provided
            String finalUrl = buildUrlWithQueryParams(url, queryParams);
            
            log.debug("Executing {} request to URL: {}", method, finalUrl);

            // Create headers
            HttpHeaders headers = createHeaders(bearerToken, customHeaders);

            // Create request entity
            HttpEntity<Object> requestEntity = new HttpEntity<>(payload, headers);

            // Execute request
            ResponseEntity<String> response = restTemplate.exchange(
                    finalUrl,
                    method,
                    requestEntity,
                    String.class
            );

            // Extract response body
            String responseBody = response.getBody();
            int statusCode = response.getStatusCode().value();

            log.debug("Response received - Status: {}, Body: {}", statusCode, responseBody);

            // Deserialize response body if not null and response type is not String
            T body = null;
            if (responseBody != null && !responseBody.isEmpty()) {
                if (responseType == String.class) {
                    @SuppressWarnings("unchecked")
                    T stringBody = (T) responseBody;
                    body = stringBody;
                } else {
                    try {
                        body = objectMapper.readValue(responseBody, responseType);
                    } catch (Exception e) {
                        log.warn("Failed to deserialize response body to {}: {}", responseType.getName(), e.getMessage());
                    }
                }
            }

            return HttpClientResponse.<T>builder()
                    .statusCode(statusCode)
                    .body(body)
                    .rawBody(responseBody)
                    .build();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle HTTP errors (4xx, 5xx)
            log.error("HTTP error occurred - Status: {}, Message: {}, Response: {}", 
                    e.getStatusCode().value(), e.getMessage(), e.getResponseBodyAsString());
            
            String responseBody = e.getResponseBodyAsString();
            T body = null;
            
            if (responseBody != null && !responseBody.isEmpty() && responseType != String.class) {
                try {
                    body = objectMapper.readValue(responseBody, responseType);
                } catch (Exception ex) {
                    log.warn("Failed to deserialize error response body: {}", ex.getMessage());
                }
            } else if (responseType == String.class) {
                @SuppressWarnings("unchecked")
                T stringBody = (T) responseBody;
                body = stringBody;
            }

            return HttpClientResponse.<T>builder()
                    .statusCode(e.getStatusCode().value())
                    .body(body)
                    .rawBody(responseBody)
                    .build();

        } catch (RestClientException e) {
            // Handle other REST client exceptions (network errors, timeouts, etc.)
            log.error("REST client exception occurred: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute HTTP request: " + e.getMessage(), e);
        }
    }

    /**
     * Build URL with query parameters
     */
    private String buildUrlWithQueryParams(String url, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return url;
        }

        StringBuilder urlBuilder = new StringBuilder(url);
        boolean firstParam = !url.contains("?");

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (firstParam) {
                urlBuilder.append("?");
                firstParam = false;
            } else {
                urlBuilder.append("&");
            }
            String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            String value = entry.getValue() != null ? 
                URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8) : "";
            urlBuilder.append(key).append("=").append(value);
        }

        return urlBuilder.toString();
    }

    /**
     * Create HTTP headers with Bearer token, X-Tenant-Id, and optional custom headers
     */
    private HttpHeaders createHeaders(String bearerToken, Map<String, String> customHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        if (bearerToken != null && !bearerToken.trim().isEmpty()) {
            // Ensure token has "Bearer " prefix
            String token = bearerToken.startsWith("Bearer ") ? bearerToken : "Bearer " + bearerToken;
            headers.set("Authorization", token);
        }

        // Automatically include X-Tenant-Id from request context
        String tenantId = requestTokenUtil.getTenantIdFromRequest();
        if (tenantId != null && !tenantId.trim().isEmpty()) {
            headers.set("X-Tenant-Id", tenantId);
        }

        // Add custom headers if provided (these will override any automatically added headers with the same name)
        if (customHeaders != null) {
            for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }

        return headers;
    }
}

