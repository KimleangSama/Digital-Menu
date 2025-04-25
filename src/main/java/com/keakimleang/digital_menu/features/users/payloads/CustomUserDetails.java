package com.keakimleang.digital_menu.features.users.payloads;


import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.entities.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;

@Getter
public class CustomUserDetails implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 5630699925975073133L;

    private final User user;

    private final List<Role> roles;

    public CustomUserDetails(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        String status = user.getStatus();
        return status.equalsIgnoreCase("active")
                || status.equalsIgnoreCase("pending");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        String status = user.getStatus();
        return status.equalsIgnoreCase("active")
                || status.equalsIgnoreCase("pending");
    }
}
