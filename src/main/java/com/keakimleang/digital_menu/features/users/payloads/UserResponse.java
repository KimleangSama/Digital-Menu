package com.keakimleang.digital_menu.features.users.payloads;

import com.keakimleang.digital_menu.features.users.entities.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class UserResponse {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String phone;
    private String address;
    private String profile;
    private String provider;
    private String status;
    private String emergencyContact;
    private String emergencyRelation;
    private LocalDateTime lastLoginAt;
    private List<Role> roles = new ArrayList<>();
}
