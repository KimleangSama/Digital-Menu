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
@Table("operating_hours")
public class OperatingHour implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String day;
    private String openTime;
    private String closeTime;

    private Long storeId;
}
