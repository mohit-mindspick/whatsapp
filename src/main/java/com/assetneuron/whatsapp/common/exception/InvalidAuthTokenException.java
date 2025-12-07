package com.assetneuron.whatsapp.common.exception;

/**
 * Exception thrown when authentication token is invalid or expired
 */
public class InvalidAuthTokenException extends RuntimeException {
    
    public InvalidAuthTokenException(String message) {
        super(message);
    }
    
    public InvalidAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

