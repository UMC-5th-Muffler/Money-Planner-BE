package com.umc5th.muffler.entity.constant;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role implements GrantedAuthority {
    USER;

    @Override
    public String getAuthority() {
        return name();
    }

    public static boolean isValidAuthority(SimpleGrantedAuthority authority) {
        for (Role role : Role.values()) {
            if (role.getAuthority().equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
