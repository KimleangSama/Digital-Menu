package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.payloads.mappers.*;
import com.keakimleang.digital_menu.features.users.services.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
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
                .map(user -> BaseResponse.<UserResponse>created().setPayload(user));
//                .onErrorResume(ex -> BaseResponseErrorHandler.handle(ex, "registering user"));
    }

    @PostMapping("/login")
    public Mono<BaseResponse<AuthResponse>> loginUser(@RequestBody LoginRequest request) {
        return authService.loginUser(request)
                .map(authResponse -> BaseResponse.<AuthResponse>ok().setPayload(authResponse))
                .switchIfEmpty(Mono.just(
                        BaseResponse.<AuthResponse>wrongCredentials()
                                .setError("Invalid credentials")));
//                .onErrorResume(ex -> BaseResponseErrorHandler.handle(ex, "logging in user"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Mono<BaseResponse<UserResponse>> findCurrentUser(@CurrentUser CustomUserDetails user) {
        if (user == null || user.getUser() == null) {
            return Mono.error(new ResponseException(
                    HttpStatus.UNAUTHORIZED.value(),
                    "error.current-user.not-found",
                    user));
        }
        return userRoleService.findRolesByUserId(user.getUser().getId())
                .collectList()
                .map(roles -> {
                    UserResponse userResponse = userMapper.toUserResponse(user.getUser());
                    userResponse.setRoles(roles);
                    return BaseResponse.<UserResponse>ok().setPayload(userResponse);
                });
    }

}
