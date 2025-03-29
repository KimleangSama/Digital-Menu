package com.keakimleang.digital_menu.features.users.repos;

import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import org.springframework.data.r2dbc.repository.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, Long> {

    Mono<Role> findByName(String name);

    Flux<Role> findByNameIn(List<String> names);
}
