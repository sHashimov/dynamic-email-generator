package com.emailgen.security;

import java.util.List;

public class JwtClaims {
    private String username;
    private List<String> roles;

    public JwtClaims(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
}

