package com.a205.mafya.api.controller;

import com.a205.mafya.api.request.AddManagerReq;
import com.a205.mafya.api.response.UserOneRes;
import com.a205.mafya.api.service.AuthService;
import com.a205.mafya.api.service.ManagerService;
import com.a205.mafya.api.service.TokenService;
import com.a205.mafya.db.dto.UserInfo;
import com.a205.mafya.db.repository.ManagerRepository;
import com.a205.mafya.util.CookieProvider;
import com.a205.mafya.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    ManagerService managerService;

    @Autowired
    AuthService authService;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    CookieProvider cookieProvider;

    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    TokenService tokenService;


    // νμ μΆκ°
    @PostMapping("")
    public ResponseEntity<?> AddManager(@RequestHeader(value="accessToken") String accessToken, HttpServletRequest req, @RequestBody AddManagerReq managerReq) throws Exception{

        managerService.addManager(managerReq);

        UserOneRes UOR = UserOneRes.builder()
                .userInfo(UserInfo.builder().userCode(managerReq.getManagerCode()).build())
                .msg("SUCCESS")
                .resultCode(0)
                .build();
        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }
}
