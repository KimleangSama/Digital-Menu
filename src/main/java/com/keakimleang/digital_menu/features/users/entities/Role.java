package com.keakimleang.digital_menu.features.users.entities;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.*;

@Getter
@Setter
@ToString
@Table("roles")
public class Role {
    @Id
    private Long id;
    private String name;
}
