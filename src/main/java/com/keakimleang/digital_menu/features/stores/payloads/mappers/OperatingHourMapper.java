package com.keakimleang.digital_menu.features.stores.payloads.mappers;

import com.keakimleang.digital_menu.features.stores.entities.OperatingHour;
import com.keakimleang.digital_menu.features.stores.payloads.request.CreateOperatingHourRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OperatingHourMapper {
    OperatingHourMapper INSTANCE = Mappers.getMapper(OperatingHourMapper.class);

    OperatingHour toOperatingHour(CreateOperatingHourRequest request);
}

