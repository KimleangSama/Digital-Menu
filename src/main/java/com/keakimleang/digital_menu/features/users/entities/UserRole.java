package com.keakimleang.digital_menu.features.users.entities;

import lombok.*;
import org.springframework.data.relational.core.mapping.*;

@Getter
@Setter
@ToString
@Table("users_roles")
public class UserRole {
    private Long userId;
    private Long roleId;
}
