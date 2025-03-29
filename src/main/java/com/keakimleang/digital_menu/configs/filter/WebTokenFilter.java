package com.keakimleang.digital_menu.configs.filter;

import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.services.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.lang.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.server.*;
import reactor.core.publisher.*;
import reactor.core.scheduler.*;

@Slf4j
@Component
public class WebTokenFilter implements WebFilter {
    @Autowired
    private TokenProviderUtils tokenProvider;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRoleServiceImpl userRoleServiceImpl;

    @Override
    public Mono<Void> filter(@NonNull final ServerWebExchange exchange,
                             @NonNull final WebFilterChain chain) {
        String jwt = getJwtFromRequest(exchange);
        if (!StringUtils.hasText(jwt)) {
            // No token provided, continue chain
            return chain.filter(exchange);
        }
        try {
            if (tokenProvider.isTokenNotExpired(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                return userService.findByUsername(username)
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(user -> userRoleServiceImpl.findRolesByUserId(user.getId())
                                .collectList()
                                .flatMap(roles -> {
                                    UsernamePasswordAuthenticationToken authentication =
                                            getUsernamePasswordAuthenticationToken(user, roles);
                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                    return chain.filter(exchange)
                                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                                })
                        )
                        .onErrorResume(e -> {
                            log.error("Authentication error", e);
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            } else {
                // Invalid token
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } catch (Exception ex) {
            log.error("Filter exception", ex);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(User user, List<Role> roles) {
        CustomUserDetails ud = new CustomUserDetails(user, roles);
        return new UsernamePasswordAuthenticationToken(
                ud,
                null,
                ud.getAuthorities()
        );
    }

    private String getJwtFromRequest(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
