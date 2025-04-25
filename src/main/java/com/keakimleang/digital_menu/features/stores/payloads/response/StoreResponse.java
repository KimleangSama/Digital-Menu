package com.keakimleang.digital_menu.features.stores.payloads.response;

import com.keakimleang.digital_menu.features.stores.entities.Store;
import com.keakimleang.digital_menu.features.stores.payloads.mappers.StoreMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class StoreResponse implements Serializable {
    private Long id;
    private String name;
    private String slug;
    private String logo;
    private String color;
    private String description;
    private String physicalAddress;
    private String virtualAddress;
    private String phone;
    private String email;
    private String website;
    private String facebook;
    private String telegram;
    private String instagram;
    private String promotion;
    private String banner;
    private String layout;
    private Double lat;
    private Double lng;
    private Boolean showGoogleMap;

    private Long createdBy;
    private boolean hasPrivilege = false;
    private Long groupId;

    private StoreInfoResponse storeInfoResponse;

    public static StoreResponse fromEntity(Store store) {
        StoreMapper mapper = StoreMapper.INSTANCE;
        StoreResponse response = mapper.toStoreResponse(store);
        response.setStoreInfoResponse(StoreInfoResponse.fromEntity(store));
        return response;
    }
}
