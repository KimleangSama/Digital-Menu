package com.keakimleang.digital_menu.configs;


import com.keakimleang.digital_menu.configs.properties.*;
import lombok.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;

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
}