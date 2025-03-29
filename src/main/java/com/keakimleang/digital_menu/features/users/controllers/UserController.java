package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.services.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

@Slf4j
@RestController
@RequestMapping(APIURLs.USERS)
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/{id}/find")
    public Mono<ResponseEntity<UserResponse>> find(@PathVariable long id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
