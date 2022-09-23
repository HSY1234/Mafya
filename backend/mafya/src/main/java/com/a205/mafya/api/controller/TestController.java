package com.a205.mafya.api.controller;

import com.a205.mafya.api.request.LoginReq;
import com.a205.mafya.api.response.LoginRes;
import com.a205.mafya.api.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    AuthService authService;


    // 로그인 테스트
    @GetMapping("")
    public ResponseEntity<?> test(@RequestHeader("accessToken") String accessToken){



        return new ResponseEntity<>("hello", HttpStatus.OK);
    }


}
