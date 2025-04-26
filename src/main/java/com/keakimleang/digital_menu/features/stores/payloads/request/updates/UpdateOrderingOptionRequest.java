package com.keakimleang.digital_menu.features.stores.payloads.request.updates;

import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.payloads.mappers.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UpdateOrderingOptionRequest {
    private Long id;
    private String name;
    private String description;
    private List<UpdateFeeRangeRequest> feeRanges;

    public static OrderingOption fromRequest(UpdateOrderingOptionRequest orderingOption) {
        OrderingOptionMapper mapper = OrderingOptionMapper.INSTANCE;
        return mapper.toOrderingOption(orderingOption);
    }
}
