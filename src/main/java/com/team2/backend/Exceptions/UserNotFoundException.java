package com.team2.backend.Exceptions;

public class UserNotFoundException extends Status400Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
