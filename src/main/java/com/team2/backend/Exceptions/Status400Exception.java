package com.team2.backend.exceptions;

public class Status400Exception extends RuntimeException {
   public Status400Exception(String msg) {
     super(msg);
   }
}