package com.keakimleang.digital_menu.features.stores.payloads.mappers;

import com.keakimleang.digital_menu.features.stores.entities.Store;
import com.keakimleang.digital_menu.features.stores.payloads.request.CreateStoreRequest;
import com.keakimleang.digital_menu.features.stores.payloads.response.StoreResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StoreMapper {
    StoreMapper INSTANCE = Mappers.getMapper(StoreMapper.class);

    Store toStore(CreateStoreRequest request);

    StoreResponse toStoreResponse(Store store);
}

