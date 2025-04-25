package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.constants.APIURLs;
import com.keakimleang.digital_menu.features.users.payloads.UserResponse;
import com.keakimleang.digital_menu.features.users.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(APIURLs.USERS)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}/find")
    public Mono<ResponseEntity<UserResponse>> find(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
