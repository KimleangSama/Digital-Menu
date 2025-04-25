package com.keakimleang.digital_menu.features.stores.payloads.mappers;

import com.keakimleang.digital_menu.features.stores.entities.FeeRange;
import com.keakimleang.digital_menu.features.stores.payloads.request.CreateFeeRangeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FeeRangeMapper {
    FeeRangeMapper INSTANCE = Mappers.getMapper(FeeRangeMapper.class);

    FeeRange toFeeRange(CreateFeeRangeRequest request);
}

