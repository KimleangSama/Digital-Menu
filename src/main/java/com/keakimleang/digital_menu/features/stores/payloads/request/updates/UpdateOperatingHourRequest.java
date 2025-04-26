package com.keakimleang.digital_menu.features.stores.payloads.request.updates;

import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.payloads.mappers.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class UpdateOperatingHourRequest {
    private Long id;
    private String day;
    private String openTime;
    private String closeTime;

    public static OperatingHour fromRequest(UpdateOperatingHourRequest request) {
        OperatingHourMapper mapper = OperatingHourMapper.INSTANCE;
        return mapper.toOperatingHour(request);
    }
}
