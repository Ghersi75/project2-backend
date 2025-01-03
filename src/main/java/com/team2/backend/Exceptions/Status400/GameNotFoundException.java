package com.team2.backend.Exceptions.Status400;

public class GameNotFoundException extends Status400Exception{
    public GameNotFoundException(String message) {
        super(message);
    }
}
