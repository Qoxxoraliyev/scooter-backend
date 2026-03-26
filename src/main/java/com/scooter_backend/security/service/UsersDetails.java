package com.scooter_backend.security.service;

import com.scooter_backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsersDetails implements UserDetails {

    private final String phone;
    private final String password;
    private final Boolean enabled;
    private final List<GrantedAuthority> authorities;


    public UsersDetails(User user) {
        this.phone = user.getPhone();
        this.password = user.getPassword();
        this.enabled = user.getEnabled();

        this.authorities = List.of(
                new SimpleGrantedAuthority(user.getRole().name())
        );


    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


}