package com.keakimleang.digital_menu.features.users.repos;

import com.keakimleang.digital_menu.features.users.entities.*;
import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Repository
public interface GroupMemberRepository extends ReactiveCrudRepository<GroupMember, Long> {

    Mono<Boolean> existsByUserIdAndGroupId(Long id, Long groupId);

    Flux<GroupMember> findByUserId(Long id);
}
