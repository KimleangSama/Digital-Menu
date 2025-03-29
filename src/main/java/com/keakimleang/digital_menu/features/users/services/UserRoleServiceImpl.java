package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public Flux<Role> findRolesByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId)
                .flatMap(ur -> roleRepository.findById(ur.getRoleId()));
    }
}