package com.keakimleang.digital_menu.features.users.services;

import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import reactor.core.publisher.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final TokenProviderUtils tokenProvider;
    private final ReactiveAuthenticationManager am;

    @Transactional
    public Mono<AuthResponse> loginUser(LoginRequest request) {
        return userService.findByUsername(request.getUsername())
                .flatMap(user -> am.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                        )
                        .flatMap(auth -> {
                            if (auth.isAuthenticated()) {
                                return userService.updateLastLoginAt(user.getId())
                                        .doOnSuccess(v -> log.info("User {} logged in", user.getUsername()))
                                        .thenReturn((CustomUserDetails) auth.getPrincipal())
                                        .map(ud -> {
                                            String accessToken = tokenProvider.generateAccessToken(ud);
                                            String refreshToken = tokenProvider.generateRefreshToken(ud);
                                            return new AuthResponse(
                                                    accessToken,
                                                    refreshToken,
                                                    user.getUsername(),
                                                    tokenProvider.getExpirationDateFromToken(accessToken)
                                            );
                                        });
                            } else {
                                return Mono.error(new BadCredentialsException("Invalid credentials"));
                            }
                        })
                        .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials"))))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

}
