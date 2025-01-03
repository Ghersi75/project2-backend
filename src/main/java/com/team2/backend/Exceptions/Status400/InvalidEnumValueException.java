package com.team2.backend.Exceptions.Status400;

public class InvalidEnumValueException extends Status400Exception{
  public InvalidEnumValueException(String msg) {
    super(msg);
  }
}