package com.keakimleang.digital_menu.features.stores.services;

import com.keakimleang.digital_menu.commons.services.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.updates.*;
import com.keakimleang.digital_menu.features.stores.payloads.response.*;
import com.keakimleang.digital_menu.features.stores.repos.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import com.keakimleang.digital_menu.utils.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import reactor.core.publisher.*;
import reactor.util.function.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final OperatingHourRepository operatingHourRepository;
    private final OrderingOptionRepository orderingOptionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final FeeRangeRepository feeRangeRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    private final ReactiveCacheService cacheService;

    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORES, key = "#user.id"),
    })
    @Transactional
    public Mono<StoreResponse> createStore(User user, CreateStoreRequest request) {
        Store store = CreateStoreRequest.fromRequest(request);
        store.setSlug(SlugifyUtils.slugify(request.getName()));
        store.setCreatedBy(user.getId());

        return storeRepository.save(store)
                .flatMap(savedStore -> {
                    // Process all related entities and collect them
                    Mono<List<OrderingOption>> orderingOptionsMono = processOrderingOptions(savedStore, request)
                            .collectList();

                    Mono<List<OperatingHour>> operatingHoursMono = processOperatingHours(savedStore, request)
                            .collectList();

                    Mono<List<PaymentMethod>> paymentMethodsMono = processPaymentMethods(savedStore, request)
                            .collectList();

                    // Combine all results
                    return Mono.zip(
                            Mono.just(savedStore),
                            orderingOptionsMono,
                            operatingHoursMono,
                            paymentMethodsMono
                    );
                })
                .map(this::buildStoreResponse)
                .doOnSuccess(savedStore -> log.info("Store {} created successfully", savedStore.getId()))
                .doOnError(e -> log.error("Error while creating store for user {}", user.getId(), e));
    }

    private Flux<OrderingOption> processOrderingOptions(Store store, CreateStoreRequest request) {
        return Flux.fromIterable(request.getOrderingOptions() != null ? request.getOrderingOptions() : Collections.emptyList())
                .flatMap(orderingOption -> {
                    OrderingOption entity = CreateOrderingOptionRequest.fromRequest(orderingOption);
                    entity.setStoreId(store.getId());

                    return orderingOptionRepository.save(entity)
                            .flatMap(savedOrderingOption -> {
                                log.info("Processing ordering option {}", savedOrderingOption.getName());

                                List<CreateFeeRangeRequest> feeRanges = orderingOption.getFeeRanges();
                                log.info("Fee ranges: {}", feeRanges);

                                if (feeRanges != null && !feeRanges.isEmpty()) {
                                    return processFeeRanges(savedOrderingOption, feeRanges)
                                            .collectList()
                                            .map(savedFeeRanges -> {
                                                savedOrderingOption.setFeeRanges(savedFeeRanges);
                                                return savedOrderingOption;
                                            });
                                } else {
                                    log.debug("No fee ranges to process for ordering option {}", savedOrderingOption.getId());
                                    return Mono.just(savedOrderingOption);
                                }
                            });
                });
    }

    private Flux<FeeRange> processFeeRanges(OrderingOption savedOrderingOption, List<CreateFeeRangeRequest> feeRanges) {
        return Flux.fromIterable(feeRanges)
                .map(feeRangeRequest -> {
                    FeeRange feeRange = CreateFeeRangeRequest.fromRequest(feeRangeRequest);
                    feeRange.setOrderingOptionId(savedOrderingOption.getId());
                    return feeRange;
                })
                .flatMap(feeRangeRepository::save);
    }

    private Flux<OperatingHour> processOperatingHours(Store store, CreateStoreRequest request) {
        return Flux.fromIterable(request.getOperatingHours() != null ? request.getOperatingHours() : Collections.emptyList())
                .map(operatingHour -> {
                    OperatingHour entity = CreateOperatingHourRequest.fromRequest(operatingHour);
                    entity.setStoreId(store.getId());
                    return entity;
                })
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(operatingHourRepository::saveAll);
    }

    private Flux<PaymentMethod> processPaymentMethods(Store store, CreateStoreRequest request) {
        return Flux.fromIterable(request.getPaymentMethods() != null ? request.getPaymentMethods() : Collections.emptyList())
                .map(paymentMethod -> {
                    PaymentMethod entity = CreatePaymentMethodRequest.fromRequest(paymentMethod);
                    entity.setStoreId(store.getId());
                    return entity;
                })
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(paymentMethodRepository::saveAll);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORES, key = "#user.id"),
    })
    @Transactional
    public Mono<StoreResponse> updateStore(User user, Long storeId, UpdateStoreRequest request) {
        return storeRepository.findById(storeId)
                .switchIfEmpty(Mono.error(new ResponseException(404, "error.store.not-found", String.valueOf(storeId))))
                .flatMap(existingStore ->
                        groupMemberRepository.existsByUserIdAndGroupId(user.getId(), existingStore.getGroupId())
                                .flatMap(hasAccess -> {
                                    if (!hasAccess) {
                                        return Mono.error(new ResponseException(403, "error.store.forbidden", user.getUsername(), storeId));
                                    }

                                    updateStoreFromRequest(existingStore, request);

                                    Mono<List<OrderingOption>> orderingOptions = recreateOrderingOptions(existingStore, request);
                                    Mono<List<OperatingHour>> operatingHours = recreateOperatingHours(existingStore, request);
                                    Mono<List<PaymentMethod>> paymentMethods = recreatePaymentMethods(existingStore, request);

                                    return storeRepository.save(existingStore)
                                            .flatMap(savedStore -> Mono.zip(orderingOptions, operatingHours, paymentMethods)
                                                    .map(tuple -> {
                                                        savedStore.setOrderingOptions(tuple.getT1());
                                                        savedStore.setOperatingHours(tuple.getT2());
                                                        savedStore.setPaymentMethods(tuple.getT3());
                                                        return savedStore;
                                                    })
                                            );
                                })
                )
                .map(StoreResponse::fromEntity)
                .doOnSuccess(response -> log.info("Store {} updated successfully", response.getId()))
                .doOnError(e -> log.error("Failed to update store {} for user {}", storeId, user.getId(), e));
    }

    private Mono<List<OrderingOption>> recreateOrderingOptions(Store store, UpdateStoreRequest request) {
        return orderingOptionRepository.deleteByStoreId(store.getId())
                .thenMany(Flux.fromIterable(request.getOrderingOptions() != null ? request.getOrderingOptions() : Collections.emptyList())
                        .flatMap(orderingOption -> {
                            OrderingOption entity = UpdateOrderingOptionRequest.fromRequest(orderingOption);
                            entity.setStoreId(store.getId());

                            return orderingOptionRepository.save(entity)
                                    .flatMap(savedOrderingOption -> {
                                        log.info("Processing updated ordering option {}", savedOrderingOption.getName());

                                        List<UpdateFeeRangeRequest> feeRanges = orderingOption.getFeeRanges();
                                        log.info("Fee ranges: {}", feeRanges);

                                        if (feeRanges != null && !feeRanges.isEmpty()) {
                                            return recreateFeeRanges(savedOrderingOption, feeRanges)
                                                    .map(feeRangesSaved -> {
                                                        savedOrderingOption.setFeeRanges(feeRangesSaved);
                                                        return savedOrderingOption;
                                                    });
                                        } else {
                                            log.debug("No fee ranges to process for ordering option {}", savedOrderingOption.getId());
                                            return Mono.just(savedOrderingOption);
                                        }
                                    });
                        }))
                .collectList();
    }

    private Mono<List<OperatingHour>> recreateOperatingHours(Store store, UpdateStoreRequest request) {
        return operatingHourRepository.deleteByStoreId(store.getId())
                .thenMany(Flux.fromIterable(request.getOperatingHours() != null ? request.getOperatingHours() : Collections.emptyList())
                        .map(operatingHour -> {
                            OperatingHour entity = UpdateOperatingHourRequest.fromRequest(operatingHour);
                            entity.setStoreId(store.getId());
                            return entity;
                        }))
                .collectList()
                .flatMapMany(list -> list.isEmpty() ? Flux.empty() : operatingHourRepository.saveAll(list))
                .collectList();
    }

    private Mono<List<PaymentMethod>> recreatePaymentMethods(Store store, UpdateStoreRequest request) {
        return paymentMethodRepository.deleteByStoreId(store.getId())
                .thenMany(Flux.fromIterable(request.getPaymentMethods() != null ? request.getPaymentMethods() : Collections.emptyList())
                        .map(paymentMethod -> {
                            PaymentMethod entity = UpdatePaymentMethodRequest.fromRequest(paymentMethod);
                            entity.setStoreId(store.getId());
                            return entity;
                        }))
                .collectList()
                .flatMapMany(list -> list.isEmpty() ? Flux.empty() : paymentMethodRepository.saveAll(list))
                .collectList();
    }

    private Mono<List<FeeRange>> recreateFeeRanges(OrderingOption savedOrderingOption, List<UpdateFeeRangeRequest> feeRanges) {
        // First delete existing fee ranges
        return feeRangeRepository.deleteByOrderingOptionId(savedOrderingOption.getId())
                .then(Flux.fromIterable(feeRanges)
                        .map(feeRangeRequest -> {
                            FeeRange feeRange = UpdateFeeRangeRequest.fromRequest(feeRangeRequest);
                            feeRange.setOrderingOptionId(savedOrderingOption.getId());
                            return feeRange;
                        })
                        .collectList()
                        .flatMapMany(feeRangeRepository::saveAll)
                        .collectList());
    }

    private void updateStoreFromRequest(Store store, UpdateStoreRequest request) {
        // Update only non-null fields from the request
        if (request.getName() != null) {
            store.setName(request.getName());
            store.setSlug(SlugifyUtils.slugify(request.getName()));
        }
        if (request.getDescription() != null) {
            store.setDescription(request.getDescription());
        }
        if (request.getPhysicalAddress() != null) {
            store.setPhysicalAddress(request.getPhysicalAddress());
        }
        if (request.getVirtualAddress() != null) {
            store.setVirtualAddress(request.getVirtualAddress());
        }
        if (request.getPhone() != null) {
            store.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            store.setEmail(request.getEmail());
        }
        if (request.getWebsite() != null) {
            store.setWebsite(request.getWebsite());
        }
        if (request.getLogo() != null) {
            store.setLogo(request.getLogo());
        }
        if (request.getBanner() != null) {
            store.setBanner(request.getBanner());
        }
        // Update any other fields from the request

        store.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    @Cacheable(value = CacheValue.STORES, key = "#slug", condition = "#slug != null")
    public Mono<StoreResponse> findStoreBySlug(String slug) {
        return storeRepository.findBySlug(slug)
                .switchIfEmpty(Mono.error(new ResponseException(404, "error.store.not-found", slug)));
    }

    @Caching(evict = {
            @CacheEvict(value = CacheValue.STORES, key = "#request.storeIds"),
    })
    public Flux<StoreResponse> assignStoreToGroup(AssignGroupRequest request) {
        return groupRepository.findById(request.getGroupId())
                .switchIfEmpty(Mono.error(new ResponseException(404, "error.group.not-found", request.getGroupId())))
                .flatMapMany(group -> storeRepository.findAllById(request.getStoreIds())
                        .switchIfEmpty(Mono.error(new ResponseException(404, "error.store.not-found", request.getStoreIds())))
                        .flatMap(store -> {
                            store.setGroupId(request.getGroupId());
                            return storeRepository.save(store);
                        })
                        .map(StoreResponse::fromEntity));
    }

    @Transactional
    public Flux<StoreResponse> findMyStore(User user) {
        return cacheService.getOrPutFlux(CacheValue.STORES, user.getId(), () ->
                groupMemberRepository.findByUserId(user.getId())
                        .switchIfEmpty(Mono.error(new ResponseException(404, "error.group-member.not-found", user.getId())))
                        .map(GroupMember::getGroupId)
                        .collectList()
                        .flatMapMany(groupIds -> storeRepository.findAllByGroupIdIn(groupIds)
                                .switchIfEmpty(Mono.error(new ResponseException(404, "error.store.not-found", user.getId())))
                        )
                        .flatMap(store -> Mono.zip(
                                Mono.just(store),
                                orderingOptionRepository.findByStoreId(store.getId())
                                        .flatMap(this::enrichOrderingOptionWithFeeRanges)
                                        .collectList(),
                                operatingHourRepository.findByStoreId(store.getId()).collectList(),
                                paymentMethodRepository.findByStoreId(store.getId()).collectList()
                        ).map(this::buildStoreResponse))
        );
    }

    private Mono<OrderingOption> enrichOrderingOptionWithFeeRanges(OrderingOption option) {
        return feeRangeRepository.findByOrderingOptionId(option.getId())
                .collectList()
                .map(feeRanges -> {
                    option.setFeeRanges(feeRanges); // Assuming you have a setter
                    return option;
                });
    }

    private StoreResponse buildStoreResponse(Tuple4<Store, List<OrderingOption>, List<OperatingHour>, List<PaymentMethod>> tuple) {
        Store storeEntity = tuple.getT1();
        List<OrderingOption> orderingOptions = tuple.getT2();
        List<OperatingHour> operatingHours = tuple.getT3();
        List<PaymentMethod> paymentMethods = tuple.getT4();

        storeEntity.setOrderingOptions(orderingOptions);
        storeEntity.setOperatingHours(operatingHours);
        storeEntity.setPaymentMethods(paymentMethods);

        return StoreResponse.fromEntity(storeEntity);
    }

    @Transactional
    public Mono<StoreResponse> updateStoreLayoutById(User user, Long id, String layout) {
        return cacheService.getOrPutMono(CacheValue.STORES, id, () -> storeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseException(404, "error.store.not-found", id)))
                .flatMap(store -> {
                    if (!store.getCreatedBy().equals(user.getId())) {
                        return Mono.error(new ResponseException(403, "error.store.forbidden", user.getUsername(), id));
                    }
                    store.setLayout(layout);
                    return storeRepository.save(store);
                })
                .map(StoreResponse::fromEntity)
                .doOnError(e -> log.error("Error while updating store layout for user {}", user.getId(), e)));
    }
}
