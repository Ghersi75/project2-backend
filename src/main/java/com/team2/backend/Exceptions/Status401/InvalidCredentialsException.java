package com.team2.backend.Exceptions.Status401;

public class InvalidCredentialsException extends Status401Exception {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
