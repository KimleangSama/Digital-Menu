package com.keakimleang.digital_menu.features.stores.repos;

import com.keakimleang.digital_menu.features.stores.entities.*;
import org.springframework.data.r2dbc.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Repository
public interface OrderingOptionRepository extends ReactiveCrudRepository<OrderingOption, Long> {
    @Modifying
    @Query("DELETE FROM ordering_options WHERE store_id = :storeId")
    Mono<Void> deleteByStoreId(@Param("storeId") Long storeId);
}
