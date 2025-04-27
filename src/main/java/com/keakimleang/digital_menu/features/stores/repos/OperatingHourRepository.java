package com.keakimleang.digital_menu.features.stores.repos;

import com.keakimleang.digital_menu.features.stores.entities.*;
import org.springframework.data.r2dbc.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.data.repository.reactive.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Repository
public interface OperatingHourRepository extends ReactiveCrudRepository<OperatingHour, Long> {
    @Modifying
    @Query("DELETE FROM operating_hours WHERE store_id = :storeId")
    Mono<Void> deleteByStoreId(@Param("storeId") Long storeId);

    Flux<OperatingHour> findByStoreId(Long id);
}
