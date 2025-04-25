package com.keakimleang.digital_menu.features.stores.controllers;

import com.keakimleang.digital_menu.annotations.CurrentUser;
import com.keakimleang.digital_menu.commons.payloads.BaseResponse;
import com.keakimleang.digital_menu.constants.APIURLs;
import com.keakimleang.digital_menu.features.stores.payloads.request.CreateStoreRequest;
import com.keakimleang.digital_menu.features.stores.payloads.response.StoreResponse;
import com.keakimleang.digital_menu.features.stores.services.StoreServiceImpl;
import com.keakimleang.digital_menu.features.users.payloads.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(APIURLs.STORE)
@RequiredArgsConstructor
public class StoreController {
    private final StoreServiceImpl storeService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin')")
    public Mono<BaseResponse<StoreResponse>> createStore(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateStoreRequest request
    ) {
        return this.storeService.createStore(user.getUser(), request)
                .map(storeResponse -> BaseResponse.<StoreResponse>ok()
                        .setPayload(storeResponse))
                .onErrorResume(e -> Mono.just(BaseResponse.<StoreResponse>exception()
                        .setError(e.getMessage())
                ));
    }
}
