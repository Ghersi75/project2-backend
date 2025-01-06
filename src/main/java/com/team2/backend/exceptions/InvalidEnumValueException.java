package com.team2.backend.exceptions;

public class InvalidEnumValueException extends Status400Exception{
  public InvalidEnumValueException(String msg) {
    super(msg);
  }
}