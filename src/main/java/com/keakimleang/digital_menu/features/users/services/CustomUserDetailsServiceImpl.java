package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserServiceImpl userService;
    private final UserRoleServiceImpl userRoleService;

    @Cacheable(value = CacheValue.USER_DETAIL, key = "#username")
    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .flatMap(user -> userRoleService.findRolesByUserId(user.getId())
                        .collectList()
                        .map(roles -> new CustomUserDetails(user, roles)));
    }
}

