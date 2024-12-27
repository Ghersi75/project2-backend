package com.team2.backend.Exceptions;

public class InvalidEnumValueException extends RuntimeException{
  public InvalidEnumValueException(String msg) {
    super(msg);
  }
}