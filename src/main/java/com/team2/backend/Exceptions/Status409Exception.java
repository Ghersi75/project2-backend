package com.team2.backend.Exceptions;

public class Status409Exception extends RuntimeException {
    public Status409Exception(String msg) {
      super(msg);
    }
  }