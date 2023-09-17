package com.simon.smile.common;

import static com.simon.smile.common.Constant.DEFAULT_PASSWORD;

public enum Person {
    ADMIN(1, "admin", DEFAULT_PASSWORD),
    INVALID(null, "invalid", DEFAULT_PASSWORD);

    private final Integer id;
    private final String username;
    private final String password;

    Person(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Integer id() {
        return id;
    }

    public String password() {
        return password;
    }

    public String username() {
        return username;
    }
}
