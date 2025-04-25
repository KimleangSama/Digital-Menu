package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.features.stores.entities.OrderingOption;
import com.keakimleang.digital_menu.features.stores.payloads.mappers.OrderingOptionMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CreateOrderingOptionRequest {
    private String name;
    private String description;
    private List<CreateFeeRangeRequest> feeRanges;

    public static OrderingOption fromRequest(CreateOrderingOptionRequest request) {
        OrderingOptionMapper mapper = OrderingOptionMapper.INSTANCE;
        return mapper.toOrderingOption(request);
    }
}
