package com.keakimleang.digital_menu.features.stores.repos;

import com.keakimleang.digital_menu.features.stores.entities.*;
import org.springframework.data.r2dbc.repository.*;
import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Repository
public interface FeeRangeRepository extends ReactiveCrudRepository<FeeRange, Long> {
    @Modifying
    @Query("DELETE FROM fee_ranges WHERE ordering_option_id = :id")
    Mono<Void> deleteByOrderingOptionId(Long id);

    Flux<FeeRange> findByOrderingOptionId(Long id);
}
