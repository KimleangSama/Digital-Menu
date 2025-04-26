package com.keakimleang.digital_menu.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResponseException extends RuntimeException {
    private final int errorCode;
    private final String messageCode;
    private final Object[] args;

    public ResponseException(int errorCode, String messageCode, Object... args) {
        super(messageCode);
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.args = args;
    }

}
