package com.assetneuron.whatsapp.common.exception;

/**
 * Exception thrown when authentication token is missing from the request
 */
public class MissingAuthTokenException extends RuntimeException {
    
    public MissingAuthTokenException(String message) {
        super(message);
    }
    
    public MissingAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

