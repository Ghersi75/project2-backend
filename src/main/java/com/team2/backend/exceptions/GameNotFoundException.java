package com.team2.backend.exceptions;

public class GameNotFoundException extends Status400Exception{
    public GameNotFoundException(String message) {
        super(message);
    }
}
