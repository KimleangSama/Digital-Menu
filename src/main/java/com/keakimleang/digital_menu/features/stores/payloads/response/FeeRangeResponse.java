package com.keakimleang.digital_menu.features.stores.payloads.response;

import com.keakimleang.digital_menu.features.stores.entities.FeeRange;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FeeRangeResponse {
    private Long id;
    private String condition;
    private Double fee;

    public static FeeRangeResponse fromEntity(FeeRange feeRange) {
        FeeRangeResponse response = new FeeRangeResponse();
        response.setId(feeRange.getId());
        response.setCondition(feeRange.getCondition());
        response.setFee(feeRange.getFee());
        return response;
    }

    public static List<FeeRangeResponse> fromEntities(List<FeeRange> feeRanges) {
        if (feeRanges == null) {
            return List.of();
        }
        return feeRanges.stream().map(FeeRangeResponse::fromEntity).toList();
    }
}
