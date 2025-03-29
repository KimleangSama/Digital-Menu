package com.keakimleang.digital_menu.features.users.services;

import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.repos.*;
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
public class AuthServiceImpl {
    private final UserRepository userRepository;
    private final TokenProviderUtils tokenProvider;
    private final ReactiveAuthenticationManager am;

    @Transactional
    public Mono<AuthResponse> loginUser(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .flatMap(user -> am.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                        )
                        .flatMap(auth -> {
                            if (auth.isAuthenticated()) {
                                CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
                                String accessToken = tokenProvider.generateAccessToken(ud);
                                String refreshToken = tokenProvider.generateRefreshToken(ud);
                                return Mono.just(new AuthResponse(
                                        accessToken,
                                        refreshToken,
                                        user.getUsername(),
                                        tokenProvider.getExpirationDateFromToken(accessToken)
                                ));
                            } else {
                                return Mono.error(new BadCredentialsException("Invalid credentials"));
                            }
                        })
                        .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials"))))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

}
