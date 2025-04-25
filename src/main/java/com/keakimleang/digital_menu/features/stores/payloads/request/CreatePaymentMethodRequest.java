package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.features.stores.entities.PaymentMethod;
import com.keakimleang.digital_menu.features.stores.payloads.mappers.PaymentMethodMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreatePaymentMethodRequest {
    private String method;

    public static PaymentMethod fromRequest(CreatePaymentMethodRequest request) {
        PaymentMethodMapper mapper = PaymentMethodMapper.INSTANCE;
        return mapper.toPaymentMethod(request);
    }

}
