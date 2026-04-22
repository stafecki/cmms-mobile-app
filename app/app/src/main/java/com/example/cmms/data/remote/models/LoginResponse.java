package com.example.cmms.data.remote.models;

public class LoginResponse {
    private UserResponse user;
    private TokenResponse tokens;

    public UserResponse getUser() { return user; }
    public TokenResponse getTokens() { return tokens; }
}