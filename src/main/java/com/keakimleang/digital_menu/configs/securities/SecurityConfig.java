package com.keakimleang.digital_menu.configs.securities;


import com.keakimleang.digital_menu.configs.filters.*;
import com.keakimleang.digital_menu.constants.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.reactive.*;
import org.springframework.security.config.web.server.*;
import org.springframework.security.web.server.*;
import org.springframework.security.web.server.context.*;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ReactiveAuthenticationManager am;
    private final ServerAuthenticationEntryPointException entryPointException;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http,
                                                         final WebTokenFilter filter) {
        return http
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(e -> {
                    e.authenticationEntryPoint(entryPointException);
                })
                .authorizeExchange(auth -> auth
                        .pathMatchers(SecurityConstant.ANONYMOUS_PATH).permitAll()
                        .anyExchange().authenticated()
                )
                .authenticationManager(am)
                .addFilterBefore(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .build();
    }
}

