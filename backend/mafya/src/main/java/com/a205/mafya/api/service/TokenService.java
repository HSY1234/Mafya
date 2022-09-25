package com.a205.mafya.api.service;

import com.a205.mafya.api.filter.exception.TokenException;
import com.a205.mafya.util.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    TokenProvider tokenProvider;

    public void TokenValidation(String accessToken, String refreshToken) throws TokenException {
        if(tokenProvider.validateToken(accessToken).equals("valid") &&
                tokenProvider.validateToken(refreshToken).equals("valid")){
            return;
        }else if(tokenProvider.validateToken(accessToken).equals("expired") &&
                tokenProvider.validateToken(refreshToken).equals("valid")){
            throw new TokenException("access token expired, refresh token valid",1,"expiredAccess",refreshToken);
        }
        else if(tokenProvider.validateToken(accessToken).equals("expired") &&
                tokenProvider.validateToken(refreshToken).equals("expired")){
            throw new TokenException("access token expired, refresh token expired",2,"expiredAR",refreshToken);
        }else {
            throw new TokenException("invalid tokens",3,"invalid",refreshToken);
        }
    }
}
