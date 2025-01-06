package com.team2.backend.exceptions;

public class Status401Exception extends RuntimeException {
    public Status401Exception(String msg) {
      super(msg);
}
}