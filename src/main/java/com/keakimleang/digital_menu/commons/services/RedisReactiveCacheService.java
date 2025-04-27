package com.keakimleang.digital_menu.commons.services;

import java.time.*;
import java.util.function.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.*;
import reactor.core.publisher.*;

@Service
@RequiredArgsConstructor
public class RedisReactiveCacheService implements ReactiveCacheService {
    @Value("${spring.cache.redis.time-to-live}")
    private Duration defaultTtl;

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> Mono<T> getOrPutMono(String cacheName, Object key, Supplier<Mono<T>> supplier) {
        String fullKey = buildCacheKey(cacheName, key);
        ReactiveValueOperations<String, Object> ops = redisTemplate.opsForValue();

        return ops.get(fullKey)
                .flatMap(cached -> Mono.just((T) cached))
                .switchIfEmpty(
                        supplier.get()
                                .flatMap(value -> ops.set(fullKey, value, defaultTtl).thenReturn(value))
                );
    }

    @Override
    public <T> Flux<T> getOrPutFlux(String cacheName, Object key, Supplier<Flux<T>> supplier) {
        String fullKey = buildCacheKey(cacheName, key);
        ReactiveValueOperations<String, Object> ops = redisTemplate.opsForValue();

        return ops.get(fullKey)
                .flatMapMany(cached -> Flux.just((T) cached))
                .switchIfEmpty(
                        supplier.get()
                                .flatMap(value -> ops.set(fullKey, value, defaultTtl).thenReturn(value))
                );
    }

    private String buildCacheKey(String cacheName, Object key) {
        return cacheName + "::" + key;
    }
}
