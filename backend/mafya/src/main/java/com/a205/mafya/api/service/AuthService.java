package com.a205.mafya.api.service;

import com.a205.mafya.api.request.LoginReq;

import com.a205.mafya.db.repository.ManagerRepository;
import com.a205.mafya.db.repository.UserRepository;
import com.a205.mafya.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ManagerRepository managerRepository;


    private final TokenProvider tokenProvider;

    public String[] login(LoginReq loginReq) {




        String [] tokens = new String[2];


        return tokens;
    }

}
