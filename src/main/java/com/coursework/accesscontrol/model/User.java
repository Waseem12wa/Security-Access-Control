package com.coursework.accesscontrol.model;

import java.util.Objects;

public final class User {
    private final String id;
    private final Role role;

    public User(String id, Role role) {
        this.id = Objects.requireNonNull(id, "id");
        this.role = Objects.requireNonNull(role, "role");
    }

    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }
}

