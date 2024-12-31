package com.team2.backend.Exceptions;

public class InvalidFavoriteGameException extends RuntimeException{
    public InvalidFavoriteGameException(String msg){
        super(msg);
    }
}
