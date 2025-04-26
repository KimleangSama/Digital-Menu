package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import reactor.core.publisher.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl {
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValue.ROLES, key = "#name")
    public Mono<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Flux<Role> findByNames(List<String> names) {
        return roleRepository.findByNameIn(names);
    }

    @Transactional(readOnly = true)
    public Flux<Role> findByIds(List<Long> ids) {
        return roleRepository.findAllById(ids);
    }

//    @Transactional(readOnly = true)
//    public List<RoleResponse> getRolesBasedOnUserRole(User user) {
//        List<Role> roles = roleRepository.findAll();
//        Set<AuthRole> userRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
//        if (userRoles.contains(AuthRole.admin)) {
//            return RoleResponse.fromEntities(roles);
//        } else if (userRoles.contains(AuthRole.manager)) {
//            return RoleResponse.fromEntitiesExclude(roles, AuthRole.admin);
//        }
//        return null;
//    }
}