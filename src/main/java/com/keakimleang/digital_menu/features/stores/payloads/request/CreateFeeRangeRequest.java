package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.features.stores.entities.FeeRange;
import com.keakimleang.digital_menu.features.stores.payloads.mappers.FeeRangeMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateFeeRangeRequest {
    private String condition;
    private Double fee;

    public static FeeRange fromRequest(CreateFeeRangeRequest request) {
        FeeRangeMapper mapper = FeeRangeMapper.INSTANCE;
        return mapper.toFeeRange(request);
    }
}
