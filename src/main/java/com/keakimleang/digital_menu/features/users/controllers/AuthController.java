package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.payloads.mappers.*;
import com.keakimleang.digital_menu.features.users.services.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.dao.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

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
                        BaseResponse.<AuthResponse>wrongCredentials()
                                .setError("Invalid credentials")))
                .onErrorResume(ex -> Mono.just(
                        BaseResponse.<AuthResponse>wrongCredentials()
                                .setError(ex.getMessage())
                ));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Mono<BaseResponse<UserResponse>> findCurrentUser(@CurrentUser CustomUserDetails user) {
        if (user == null || user.getUser() == null) {
            return Mono.just(BaseResponse.<UserResponse>badRequest()
                    .setError("User is not authenticated or user details are missing."));
        }
        return userRoleService.findRolesByUserId(user.getUser().getId())
                .collectList()
                .map(roles -> {
                    UserResponse userResponse = userMapper.toUserResponse(user.getUser());
                    userResponse.setRoles(roles);
                    return BaseResponse.<UserResponse>ok().setPayload(userResponse);
                })
                .onErrorResume(ex -> {
                    log.error("Error fetching current user", ex);
                    return Mono.just(BaseResponse.<UserResponse>exception()
                            .setError("Failed to fetch current user: " + ex.getMessage()));
                });
    }

}
