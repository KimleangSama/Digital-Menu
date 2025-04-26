package com.keakimleang.digital_menu.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceForbiddenException extends RuntimeException {
    private final String username;
    private final transient String resource;

    public ResourceForbiddenException(String username, String resource) {
        super("User: " + username + " is not allowed to access this resource: " + resource + ".");
        this.username = username;
        this.resource = resource;
    }
}
