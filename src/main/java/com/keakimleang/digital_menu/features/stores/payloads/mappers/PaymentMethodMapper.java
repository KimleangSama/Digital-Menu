package com.keakimleang.digital_menu.features.stores.payloads.mappers;

import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.*;
import com.keakimleang.digital_menu.features.stores.payloads.request.updates.*;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethodMapper INSTANCE = Mappers.getMapper(PaymentMethodMapper.class);

    PaymentMethod toPaymentMethod(CreatePaymentMethodRequest request);

    PaymentMethod toPaymentMethod(UpdatePaymentMethodRequest request);
}

