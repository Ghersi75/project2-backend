package com.team2.backend.exceptions;

public class InvalidFavoriteGameException extends Status409Exception{
    public InvalidFavoriteGameException(String msg){
        super(msg);
    }
}
