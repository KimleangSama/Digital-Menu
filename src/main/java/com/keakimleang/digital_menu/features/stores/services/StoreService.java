package com.keakimleang.digital_menu.features.stores.services;

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
                .map(tuple -> {
                    Store savedStore = tuple.getT1();
                    List<OrderingOption> orderingOptions = tuple.getT2();
                    List<OperatingHour> operatingHours = tuple.getT3();
                    List<PaymentMethod> paymentMethods = tuple.getT4();

                    // Associate the saved entities with the store
                    savedStore.setOrderingOptions(orderingOptions);
                    savedStore.setOperatingHours(operatingHours);
                    savedStore.setPaymentMethods(paymentMethods);

                    return StoreResponse.fromEntity(savedStore);
                })
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
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Store", "" + storeId)))
                .flatMap(existingStore -> groupMemberRepository.existsByUserIdAndGroupId(user.getId(), existingStore.getGroupId())
                        .flatMap(exists -> {
                            if (exists) {
                                // Update basic store properties
                                updateStoreFromRequest(existingStore, request);
                                return storeRepository.save(existingStore)
                                        .flatMap(savedStore -> {
                                            // Collect all operations that create related entities
                                            Mono<List<OrderingOption>> orderingOptionsMono = recreateOrderingOptions(savedStore, request);
                                            Mono<List<OperatingHour>> operatingHoursMono = recreateOperatingHours(savedStore, request);
                                            Mono<List<PaymentMethod>> paymentMethodsMono = recreatePaymentMethods(savedStore, request);

                                            // Wait for all operations to complete and collect their results
                                            return Mono.zip(
                                                    orderingOptionsMono,
                                                    operatingHoursMono,
                                                    paymentMethodsMono
                                            ).map(tuple -> {
                                                // Set related entities on the store for response
                                                savedStore.setOrderingOptions(tuple.getT1());
                                                savedStore.setOperatingHours(tuple.getT2());
                                                savedStore.setPaymentMethods(tuple.getT3());
                                                return savedStore;
                                            });
                                        })
                                        .map(StoreResponse::fromEntity)
                                        .doOnSuccess(updatedStore -> log.info("Store {} updated successfully", updatedStore.getId()))
                                        .doOnError(e -> log.error("Error while updating store {} for user {}", storeId, user.getId(), e));
                            } else {
                                // Add this else clause to handle the case where permission check fails
                                return Mono.error(new ResourceForbiddenException(user.getUsername(), "store with ID: " + storeId));
                            }
                        }));
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

}
