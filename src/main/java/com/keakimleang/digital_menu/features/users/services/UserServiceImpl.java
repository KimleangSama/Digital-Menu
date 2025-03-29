package com.keakimleang.digital_menu.features.users.services;

import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import com.keakimleang.digital_menu.features.users.payloads.mappers.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import reactor.core.publisher.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRoleServiceImpl userRoleService;
    private final RoleServiceImpl roleService;

    @Transactional
    public Mono<UserResponse> registerUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setRaw(request.getPassword());
        user.setEmail(request.getFullname());
        user.setProvider("local");
        user.setStatus("pending");

        return userRepository.save(user)
                .flatMap(savedUser ->
                        Flux.fromIterable(request.getRoles())
                                .flatMap(role -> roleService.findByName(role)
                                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found: " + role)))
                                        .flatMap(roleEntity -> {
                                            UserRole userRole = new UserRole();
                                            userRole.setUserId(savedUser.getId());
                                            userRole.setRoleId(roleEntity.getId());
                                            return userRoleRepository.save(userRole)
                                                    .thenReturn(roleEntity);
                                        })
                                ).collectList()
                                .map(roles -> {
                                    UserResponse response = userMapper.toUserResponse(savedUser);
                                    response.setRoles(roles);
                                    return response;
                                })
                )
                .onErrorMap(e -> {
                    // You can further refine which exceptions to catch and map to your custom exception
                    if (e instanceof IllegalArgumentException) {
                        return new RuntimeException("User registration failed: " + e.getMessage());
                    } else {
                        return new RuntimeException("User registration failed due to an unexpected error: " + e.getMessage());
                    }
                });
    }

    @Transactional
    public Mono<UserResponse> findUserById(Long id) {
        return userRepository.findById(id)
                .flatMap(user -> userRoleService.findRolesByUserId(user.getId())  // Fetch roles
                        .collectList()
                        .map(roles -> {
                            UserResponse response = userMapper.toUserResponse(user);
                            response.setRoles(roles);
                            return response;
                        })
                );
    }

    @Cacheable(value = CacheValue.USER, key = "#username")
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }
}
