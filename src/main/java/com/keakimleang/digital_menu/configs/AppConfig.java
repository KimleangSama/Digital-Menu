package com.keakimleang.digital_menu.configs;


import com.keakimleang.digital_menu.configs.properties.*;
import java.util.*;
import lombok.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.web.server.i18n.*;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {
        TokenProperties.class,
        CORSProperties.class,
        LogProperties.class
})
public class AppConfig {
    private final ReactiveUserDetailsService us;

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager am =
                new UserDetailsRepositoryReactiveAuthenticationManager(us);
        am.setPasswordEncoder(passwordEncoder());
        return am;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for an hour
        return messageSource;
    }

    @Bean
    public AcceptHeaderLocaleContextResolver localeResolver() {
        AcceptHeaderLocaleContextResolver localeResolver = new AcceptHeaderLocaleContextResolver();
        localeResolver.setDefaultLocale(Locale.US); // Set the default locale if none is specified
        return localeResolver;
    }
}