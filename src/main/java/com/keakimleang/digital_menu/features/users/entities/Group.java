package com.keakimleang.digital_menu.features.users.entities;

import com.keakimleang.digital_menu.commons.entities.BaseEntityAudit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;

@Getter
@Setter
@ToString
@Table(name = "groups")
public class Group extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
}