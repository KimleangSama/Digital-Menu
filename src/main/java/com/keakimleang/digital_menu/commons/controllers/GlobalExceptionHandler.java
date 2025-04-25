package com.keakimleang.digital_menu.commons.controllers;

import com.keakimleang.digital_menu.commons.payloads.BaseResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public BaseResponse<Object> handleAuthException(AuthenticationCredentialsNotFoundException ex) {
        return BaseResponse
                .unauthorized()
                .setError("User is not authenticated.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public BaseResponse<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return BaseResponse
                .accessDenied()
                .setError("User is not authorized to access this resource.");
    }
}

