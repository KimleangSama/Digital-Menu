package com.keakimleang.digital_menu.features.stores.services;

import com.keakimleang.digital_menu.constants.CacheValue;
import com.keakimleang.digital_menu.features.stores.entities.Store;
import com.keakimleang.digital_menu.features.stores.payloads.request.CreateStoreRequest;
import com.keakimleang.digital_menu.features.stores.payloads.response.StoreResponse;
import com.keakimleang.digital_menu.features.stores.repos.StoreRepository;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.utils.SlugifyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl {
    private final StoreRepository storeRepository;

    // Evict only to creator's list of stores
    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORES, key = "#user.id"),
    })
    @Transactional
    public Mono<StoreResponse> createStore(User user, CreateStoreRequest request) {
        try {
            Store store = CreateStoreRequest.fromRequest(request);
            store.setSlug(SlugifyUtils.slugify(request.getName()));
            store.setCreatedBy(user.getId());
            return this.storeRepository.save(store)
                    .map(StoreResponse::fromEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        } catch (Exception e) {
            log.error("Error while creating store", e);
            throw new RuntimeException("Error while creating store", e);
        }
    }
}
