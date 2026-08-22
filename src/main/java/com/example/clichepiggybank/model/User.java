package com.example.clichepiggybank.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String[] roles;

    public User(UUID id, String name, String[] roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
