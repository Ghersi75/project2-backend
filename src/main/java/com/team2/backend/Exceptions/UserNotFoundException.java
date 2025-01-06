package com.team2.backend.exceptions;

public class UserNotFoundException extends Status400Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
