package com.keakimleang.digital_menu.configs.filters;

import com.keakimleang.digital_menu.features.users.entities.Role;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.payloads.CustomUserDetails;
import com.keakimleang.digital_menu.features.users.services.UserRoleService;
import com.keakimleang.digital_menu.features.users.services.UserService;
import com.keakimleang.digital_menu.utils.TokenProviderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Component
public class WebTokenFilter implements WebFilter {
    @Autowired
    private TokenProviderUtils tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;

    @NonNull
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
                        .flatMap(user -> userRoleService.findRolesByUserId(user.getId())
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
