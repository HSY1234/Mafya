package com.a205.mafya.api.filter.exception;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message) { super(message);
    }
}
