package com.keakimleang.digital_menu.features.users.payloads;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class UserRequest {
    private String username;
    private String password;
    private String fullname;
    private List<String> roles = new ArrayList<>();
}
