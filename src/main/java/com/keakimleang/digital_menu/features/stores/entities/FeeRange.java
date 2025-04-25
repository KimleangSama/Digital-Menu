package com.keakimleang.digital_menu.features.stores.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Table(name = "fee_ranges")
public class FeeRange implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String condition;
    private Double fee;

    private Long orderingOptionId;
}
