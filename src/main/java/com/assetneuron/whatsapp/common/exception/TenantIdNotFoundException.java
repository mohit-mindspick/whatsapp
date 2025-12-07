package com.assetneuron.whatsapp.common.exception;

/**
 * Exception thrown when tenant ID is not found in the authentication token
 */
public class TenantIdNotFoundException extends RuntimeException {
    
    public TenantIdNotFoundException(String message) {
        super(message);
    }
    
    public TenantIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

