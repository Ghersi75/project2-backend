package com.team2.backend.dto;

public class UserResponseDTO {
    private String message;
    private String token; // Optional: Include if you're using JWT or session tokens.

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}
