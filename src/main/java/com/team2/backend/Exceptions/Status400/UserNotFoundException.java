package com.team2.backend.Exceptions.Status400;

public class UserNotFoundException extends Status400Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
