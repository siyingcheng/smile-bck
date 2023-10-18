package com.simon.smile.common;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import static com.simon.smile.common.Constant.DEFAULT_PASSWORD;

@AllArgsConstructor
public enum Person {
    ADMIN(1, "admin", DEFAULT_PASSWORD, "admin@example.com"),
    INVALID(null, "invalid", DEFAULT_PASSWORD, "invalid@example.com");

    private final Integer id;
    private final String username;
    private final String password;
    private final String email;

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    public String username() {
        return username;
    }
}
