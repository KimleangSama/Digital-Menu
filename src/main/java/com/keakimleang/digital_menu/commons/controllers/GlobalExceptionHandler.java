package com.keakimleang.digital_menu.commons.controllers;

import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.*;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthenticationCredentialsNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.
                status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("message", ex.getMessage()));
    }
}

