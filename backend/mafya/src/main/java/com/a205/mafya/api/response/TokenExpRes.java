package com.a205.mafya.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TokenExpRes extends BasicRes{

    private String tokenStatus;
    private String accessToken;

    public void changeTokenStatus(String tokenStatus){
        this.tokenStatus = tokenStatus;
    }
    public void changeAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

}
