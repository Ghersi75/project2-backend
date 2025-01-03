package com.team2.backend.Exceptions;

public class InvalidCredentialsException extends Status401Exception {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
