package com.keakimleang.digital_menu.commons.services;

import java.util.function.*;
import reactor.core.publisher.*;

public interface ReactiveCacheService {
    <T> Mono<T> getOrPutMono(String cacheName, Object key, Supplier<Mono<T>> supplier);

    <T> Flux<T> getOrPutFlux(String cacheName, Object key, Supplier<Flux<T>> supplier);
}
