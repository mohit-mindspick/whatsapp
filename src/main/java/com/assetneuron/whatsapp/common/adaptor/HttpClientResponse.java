package com.assetneuron.whatsapp.common.adaptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response wrapper for HTTP client calls
 * Contains the HTTP status code and response body
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpClientResponse<T> {
    private int statusCode;
    private T body;
    private String rawBody;
}

