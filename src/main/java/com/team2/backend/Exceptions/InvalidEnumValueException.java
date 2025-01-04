package com.team2.backend.exceptions;

public class InvalidEnumValueException extends RuntimeException{
  public InvalidEnumValueException(String msg) {
    super(msg);
  }
}