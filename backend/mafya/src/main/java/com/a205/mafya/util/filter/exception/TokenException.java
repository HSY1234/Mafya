package com.a205.mafya.util.filter.exception;

import lombok.Getter;

import java.io.IOException;

@Getter
public class TokenException extends IOException {


    private int resultCode;
    private String tokenStatus;
    private String oldRefreshToken;

    public TokenException(String msg, int resultCode, String tokenStatus, String oldRefreshToken){
        super(msg);
        this.resultCode = resultCode;
        this.tokenStatus = tokenStatus;
        this.oldRefreshToken = oldRefreshToken;

    }


}
