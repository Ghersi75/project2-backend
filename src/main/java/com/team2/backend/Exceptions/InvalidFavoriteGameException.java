package com.team2.backend.Exceptions;

public class InvalidFavoriteGameException extends Status409Exception{
    public InvalidFavoriteGameException(String msg){
        super(msg);
    }
}
