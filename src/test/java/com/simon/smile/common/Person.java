package com.simon.smile.common;

import lombok.AllArgsConstructor;

import static com.simon.smile.common.Constant.DEFAULT_PASSWORD;

@AllArgsConstructor
public enum Person {
    ADMIN(1, "admin", DEFAULT_PASSWORD),
    INVALID(null, "invalid", DEFAULT_PASSWORD);

    private final Integer id;
    private final String username;
    private final String password;

    public String password() {
        return password;
    }

    public String username() {
        return username;
    }
}
