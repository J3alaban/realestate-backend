package com.realestate.backend.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    CUSTOMER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
