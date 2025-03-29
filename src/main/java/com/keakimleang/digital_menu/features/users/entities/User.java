package com.keakimleang.digital_menu.features.users.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import java.time.*;
import lombok.*;
import org.springframework.data.relational.core.mapping.*;

@Getter
@Setter
@ToString
@Table("users")
public class User extends BaseEntityAudit {
    private String username;
    private String password;
    private String raw;
    private String email;
    private String fullname;
    private String phone;
    private String address;
    private String profileUrl;
    private String provider;
    private String status;
    private String emergencyContact;
    private String emergencyRelation;
    private LocalDateTime lastLoginAt;

    private Long deletedBy;
    private LocalDateTime deletedAt;
}
