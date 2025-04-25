package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.features.stores.entities.OperatingHour;
import com.keakimleang.digital_menu.features.stores.payloads.mappers.OperatingHourMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
public class CreateOperatingHourRequest {
    private String day;
    private String openTime;
    private String closeTime;

    public static OperatingHour fromRequest(CreateOperatingHourRequest request) {
        OperatingHourMapper mapper = OperatingHourMapper.INSTANCE;
        return mapper.toOperatingHour(request);
    }
}
