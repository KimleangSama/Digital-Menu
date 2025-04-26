package com.keakimleang.digital_menu.configs.securities;

import com.fasterxml.jackson.databind.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.context.*;
import org.springframework.context.i18n.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.web.server.*;
import org.springframework.stereotype.*;
import org.springframework.web.server.*;
import reactor.core.publisher.*;

@Component
@RequiredArgsConstructor
public class ServerAuthenticationEntryPointException implements ServerAuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        Locale locale = LocaleContextHolder.getLocale();

        String localizedMessage = messageSource.getMessage(
                "error.unauthorized", null, "Unauthorized", locale);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "Unauthorized Error");
        responseMap.put("success", false);
        responseMap.put("message", localizedMessage);
        responseMap.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        responseMap.put("timestamp", LocalDateTime.now());

        byte[] responseBytes;
        try {
            responseBytes = objectMapper.writeValueAsBytes(responseMap);
        } catch (Exception e) {
            responseBytes = "{\"status\":\"error\",\"message\":\"Serialization error\"}".getBytes(StandardCharsets.UTF_8);
        }

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBytes)));
    }
}
