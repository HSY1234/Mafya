package com.a205.mafya.api.service;

import com.a205.mafya.api.request.LoginReq;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.UserRepository;
import com.a205.mafya.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    UserRepository userRepository;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    public String[] login(LoginReq loginReq) {


        // userCode, password를 파라미터로 받고 이를 이용해 UsernamePasswordAuthenticationToken을 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginReq.getUserCode(), loginReq.getPassword());

        // authenticationToken을 이용해서 Authenticaiton 객체를 생성하려고 authenticate 메소드가 실행될 때
        // CustomUserDetailsService에서 override한 loadUserByUsername 메소드가 실행된다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // authentication 을 기준으로 jwt token 생성
        String accessToken = tokenProvider.createToken(authentication, 'a');
        // authentication 을 기준으로 jwt token 생성
        String refreshToken = tokenProvider.createToken(authentication, 'r');

        String [] tokens = new String[2];
        tokens[0] = accessToken;
        tokens[1] = refreshToken;

        return tokens;
    }

}
