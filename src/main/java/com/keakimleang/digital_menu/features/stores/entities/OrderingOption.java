package com.keakimleang.digital_menu.features.stores.entities;

import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Table("ordering_options")
public class OrderingOption implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String name;
    private String description;

    private Long storeId;

    @Transient
    private List<FeeRange> feeRanges;
}
