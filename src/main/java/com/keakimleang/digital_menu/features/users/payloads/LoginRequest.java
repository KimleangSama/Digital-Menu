package com.keakimleang.digital_menu.features.users.payloads;

import lombok.*;

@Getter
@Setter
@ToString
public class LoginRequest {
    private String username;
    private String password;
}
