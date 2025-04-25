package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.constants.CacheValue;
import com.keakimleang.digital_menu.features.users.entities.Role;
import com.keakimleang.digital_menu.features.users.repos.RoleRepository;
import com.keakimleang.digital_menu.features.users.repos.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Cacheable(value = CacheValue.ROLES, key = "#userId")
    public Flux<Role> findRolesByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId)
                .flatMap(ur -> roleRepository.findById(ur.getRoleId()));
    }
}