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
public class UpdateFeeRangeRequest {
    private Long id;
    private String condition;
    private Double fee;

    public static FeeRange fromRequest(UpdateFeeRangeRequest feeRangeRequest) {
        FeeRangeMapper mapper = FeeRangeMapper.INSTANCE;
        return mapper.toFeeRange(feeRangeRequest);
    }
}
