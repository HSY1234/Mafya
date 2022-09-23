package com.a205.mafya.api.filter.exception;


import lombok.Getter;

public class UserCodeOverlapException extends RuntimeException {
    public UserCodeOverlapException(String message) { super(message);
    }

}
