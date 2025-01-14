package com.myme.mycarforme.domains.user.domain;

import java.util.Arrays;

public enum UserRole {
    MEMBER("member"),
    ADMIN("admin");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static UserRole from(String role) {
        return Arrays.stream(values())
                .filter(r -> r.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role));
    }
}
