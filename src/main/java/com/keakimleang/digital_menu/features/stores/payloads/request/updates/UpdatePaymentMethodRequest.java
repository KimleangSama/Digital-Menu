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
public class UpdatePaymentMethodRequest {
    private Long id;
    private String method;

    public static PaymentMethod fromRequest(UpdatePaymentMethodRequest request) {
        PaymentMethodMapper mapper = PaymentMethodMapper.INSTANCE;
        return mapper.toPaymentMethod(request);
    }
}
