package com.keakimleang.digital_menu.features.users.payloads;

import com.keakimleang.digital_menu.features.users.entities.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class UserResponse {
    private Long id;
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
    private List<Role> roles = new ArrayList<>();
}
