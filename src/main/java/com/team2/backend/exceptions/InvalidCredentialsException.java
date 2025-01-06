package com.team2.backend.exceptions;

public class InvalidCredentialsException extends Status401Exception {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
