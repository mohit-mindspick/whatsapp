package com.assetneuron.whatsapp.common.exception;

import com.assetneuron.whatsapp.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    private static final String ERR_BAD_REQUEST = "ERR_BAD_REQUEST";

    /**
     * Handle MissingAuthTokenException - returns 401 Unauthorized
     */
    @ExceptionHandler(MissingAuthTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingAuthTokenException(MissingAuthTokenException e) {
        log.warn("Missing authentication token: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Object>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }

    /**
     * Handle InvalidAuthTokenException - returns 401 Unauthorized
     */
    @ExceptionHandler(InvalidAuthTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidAuthTokenException(InvalidAuthTokenException e) {
        log.warn("Invalid authentication token: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Object>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }

    /**
     * Handle TenantIdNotFoundException - returns 400 Bad Request
     */
    @ExceptionHandler(TenantIdNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleTenantIdNotFoundException(TenantIdNotFoundException e) {
        log.warn("Tenant ID not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Method argument validation exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>builder()
                        .success(false)
                        .message(ERR_BAD_REQUEST)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>builder()
                        .success(false)
                        .message(ERR_BAD_REQUEST)
                        .build());
    }
}

