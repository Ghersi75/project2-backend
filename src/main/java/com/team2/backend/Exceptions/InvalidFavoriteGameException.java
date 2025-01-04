package com.team2.backend.exceptions;

public class InvalidFavoriteGameException extends RuntimeException{
    public InvalidFavoriteGameException(String msg){
        super(msg);
    }
}
