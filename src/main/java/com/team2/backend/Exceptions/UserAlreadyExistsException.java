package com.team2.backend.Exceptions;

public class UserAlreadyExistsException extends Status409Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}