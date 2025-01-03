package com.team2.backend.Exceptions.Status409;

public class InvalidFavoriteGameException extends Status409Exception{
    public InvalidFavoriteGameException(String msg){
        super(msg);
    }
}
