package com.keakimleang.digital_menu.commons.controllers;

import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.exceptions.*;
import java.util.*;
import org.springframework.context.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResponseException.class)
    public BaseResponse<Object> handleLocalizedException(
            ResponseException ex, Locale locale) {

        String localizedMessage = messageSource.getMessage(
                ex.getMessageCode(), ex.getArgs(), ex.getMessageCode(), locale == null ? Locale.getDefault() : locale);

        if (ex.getErrorCode() == HttpStatus.NOT_FOUND.value()) {
            return BaseResponse.notFound().setError(localizedMessage);
        } else if (ex.getErrorCode() == HttpStatus.BAD_REQUEST.value()) {
            return BaseResponse.badRequest().setError(localizedMessage);
        } else if (ex.getErrorCode() == HttpStatus.UNAUTHORIZED.value()) {
            return BaseResponse.unauthorized().setError(localizedMessage);
        } else if (ex.getErrorCode() == HttpStatus.FORBIDDEN.value()) {
            return BaseResponse.accessDenied().setError(localizedMessage);
        } else if (ex.getErrorCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return BaseResponse.exception().setError(localizedMessage);
        } else {
            return BaseResponse.exception().setError(localizedMessage);
        }
    }
}
