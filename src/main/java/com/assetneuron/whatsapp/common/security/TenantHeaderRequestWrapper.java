package com.assetneuron.whatsapp.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TenantHeaderRequestWrapper extends HttpServletRequestWrapper {
    
    private final Map<String, String> additionalHeaders = new HashMap<>();
    
    public TenantHeaderRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    public void addHeader(String name, String value) {
        additionalHeaders.put(name, value);
    }
    
    @Override
    public String getHeader(String name) {
        // Check additional headers first
        String additionalHeader = additionalHeaders.get(name);
        if (additionalHeader != null) {
            return additionalHeader;
        }
        // Fall back to original request
        return super.getHeader(name);
    }
    
    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> headerNames = new HashSet<>();
        // Add original header names
        Enumeration<String> originalHeaders = super.getHeaderNames();
        while (originalHeaders.hasMoreElements()) {
            headerNames.add(originalHeaders.nextElement());
        }
        // Add additional header names
        headerNames.addAll(additionalHeaders.keySet());
        return Collections.enumeration(headerNames);
    }
    
    @Override
    public Enumeration<String> getHeaders(String name) {
        // Check additional headers first
        if (additionalHeaders.containsKey(name)) {
            return Collections.enumeration(Arrays.asList(additionalHeaders.get(name)));
        }
        // Fall back to original request
        return super.getHeaders(name);
    }
}
