package com.keakimleang.digital_menu.features.stores.payloads.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AssignGroupRequest {
    private Long groupId;
    private List<Long> storeIds;
}
