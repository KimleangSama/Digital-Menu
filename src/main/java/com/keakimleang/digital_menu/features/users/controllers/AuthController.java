package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.payloads.mappers.*;
import com.keakimleang.digital_menu.features.users.services.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

@Slf4j
@RestController
@RequestMapping(APIURLs.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authService;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public Mono<UserResponse> registerUser(@RequestBody UserRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> loginUser(@RequestBody LoginRequest request) {
        return authService.loginUser(request);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Mono<UserResponse> findCurrentUser(@CurrentUser CustomUserDetails user) {
        log.info("Get current user {}", user);
        return Mono.just(user.getUser())
                .map(userMapper::toUserResponse);
    }
}
