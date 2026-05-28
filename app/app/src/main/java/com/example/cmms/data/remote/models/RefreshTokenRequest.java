package com.example.cmms.data.remote.models;

public class RefreshTokenRequest {
    private final String token;

    public RefreshTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
}
