package com.team2.backend.Exceptions.Status409;

public class UserAlreadyExistsException extends Status409Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}