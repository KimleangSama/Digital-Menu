package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.CurrentUser;
import com.keakimleang.digital_menu.commons.payloads.BaseResponse;
import com.keakimleang.digital_menu.constants.APIURLs;
import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.payloads.mappers.UserMapper;
import com.keakimleang.digital_menu.features.users.services.AuthService;
import com.keakimleang.digital_menu.features.users.services.UserRoleService;
import com.keakimleang.digital_menu.features.users.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(APIURLs.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;

    @PostMapping("/register")
    public Mono<BaseResponse<UserResponse>> registerUser(@RequestBody UserRequest request) {
        return userService.registerUser(request)
                .map(user -> BaseResponse.<UserResponse>created().setPayload(user))
                .onErrorResume(ex -> {
                    if (ex instanceof DataIntegrityViolationException) {
                        return Mono.just(BaseResponse.<UserResponse>badRequest()
                                .setError("User info request is invalid: " + ex.getMessage()));
                    }
                    return Mono.just(BaseResponse.<UserResponse>exception()
                            .setError(ex.getMessage()));
                });
    }

    @PostMapping("/login")
    public Mono<BaseResponse<AuthResponse>> loginUser(@RequestBody LoginRequest request) {
        return authService.loginUser(request)
                .map(authResponse -> BaseResponse.<AuthResponse>ok().setPayload(authResponse))
                .switchIfEmpty(Mono.just(
                        BaseResponse.<AuthResponse>badRequest()
                                .setError("Invalid credentials")))
                .onErrorResume(ex -> Mono.just(
                        BaseResponse.<AuthResponse>wrongCredentials()
                                .setError(ex.getMessage())
                ));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Mono<BaseResponse<UserResponse>> findCurrentUser(@CurrentUser CustomUserDetails user) {
        return Mono
                .fromCallable(() -> userRoleService.findRolesByUserId(user.getUser().getId()))
                .flatMap(rolesFlux -> rolesFlux.collectList()
                        .map(roles -> {
                            UserResponse userResponse = userMapper.toUserResponse(user.getUser());
                            userResponse.setRoles(roles);
                            return BaseResponse.<UserResponse>ok().setPayload(userResponse);
                        })
                )
                .onErrorResume(ex -> {
                    log.error("Error fetching current user: {}", ex.getMessage());
                    return Mono.just(BaseResponse.<UserResponse>exception()
                            .setError("Failed to fetch current user: " + ex.getMessage()));
                });
    }
}
