package com.keakimleang.digital_menu.features.users.entities;

import com.keakimleang.digital_menu.commons.entities.BaseEntityAudit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Table("users")
public class User extends BaseEntityAudit {
    private String username;
    private String encryptedPassword;
    private String rawPassword;
    private String email;
    private String fullname;
    private String profile;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyRelation;
    private String provider;
    private String status;
    private LocalDateTime lastLoginAt;

    @Transient
    private List<Role> roles;
}
