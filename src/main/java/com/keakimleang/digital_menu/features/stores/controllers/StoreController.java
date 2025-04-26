package com.keakimleang.digital_menu.features.stores.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.updates.*;
import com.keakimleang.digital_menu.features.stores.payloads.response.*;
import com.keakimleang.digital_menu.features.stores.services.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

@Slf4j
@RestController
@RequestMapping(APIURLs.STORE)
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin')")
    public Mono<BaseResponse<StoreResponse>> createStore(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateStoreRequest request
    ) {
        return storeService.createStore(user.getUser(), request)
                .map(storeResponse -> BaseResponse.<StoreResponse>ok().setPayload(storeResponse))
                .onErrorResume(e -> Mono.just(
                        BaseResponse.<StoreResponse>exception()
                                .setError("Error creating store: " + e.getMessage())
                ));
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public Mono<BaseResponse<StoreResponse>> updateStoreById(
            @CurrentUser CustomUserDetails user,
            @PathVariable Long id,
            @RequestBody UpdateStoreRequest request
    ) {
        return storeService.updateStore(user.getUser(), id, request)
                .map(storeResponse -> BaseResponse.<StoreResponse>ok().setPayload(storeResponse))
                .onErrorResume(e -> Mono.just(
                        BaseResponse.<StoreResponse>exception()
                                .setError("Error updating store: " + e.getMessage())
                ));
    }
}
