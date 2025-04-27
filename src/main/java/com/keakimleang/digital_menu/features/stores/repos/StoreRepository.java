package com.keakimleang.digital_menu.features.stores.repos;

import com.keakimleang.digital_menu.features.stores.entities.Store;
import com.keakimleang.digital_menu.features.stores.payloads.response.*;
import java.util.*;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.*;

@Repository
public interface StoreRepository extends ReactiveCrudRepository<Store, Long> {
    Mono<StoreResponse> findBySlug(String slug);

    Flux<Store> findAllByGroupIdIn(List<Long> groupIds);
}
