package com.keakimleang.digital_menu.features.stores.payloads.mappers;

import com.keakimleang.digital_menu.features.stores.entities.OrderingOption;
import com.keakimleang.digital_menu.features.stores.entities.PaymentMethod;
import com.keakimleang.digital_menu.features.stores.payloads.request.CreateOrderingOptionRequest;
import com.keakimleang.digital_menu.features.stores.payloads.request.CreatePaymentMethodRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderingOptionMapper {
    OrderingOptionMapper INSTANCE = Mappers.getMapper(OrderingOptionMapper.class);

    OrderingOption toOrderingOption(CreateOrderingOptionRequest request);
}

