package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Cacheable(value = CacheValue.ROLES, key = "#userId", condition = "#userId != null")
    public Flux<Role> findRolesByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId)
                .map(UserRole::getRoleId)
                .distinct()
                .collectList()
                .flatMapMany(roleRepository::findAllById);
    }
}