package com.keakimleang.digital_menu.features.stores.entities;

import com.keakimleang.digital_menu.commons.entities.BaseEntityAudit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;

@Getter
@Setter
@ToString
@Table(name = "stores")
public class Store extends BaseEntityAudit {
    @Serial
    private final static long serialVersionUID = 1L;

    private String name;
    private String slug;
    private String logo;
    private String color = "#D22530";
    private String description;
    private String physicalAddress;
    private String virtualAddress;
    private String phone;
    private String email;
    private String website;
    private String facebook;
    private String telegram;
    private String instagram;
    private String promotion;
    private String banner;
    private String layout;
    private Double lat;
    private Double lng;
    private Boolean showGoogleMap = true;

    private Long groupId;
}
