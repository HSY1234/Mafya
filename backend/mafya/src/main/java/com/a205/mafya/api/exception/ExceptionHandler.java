package com.a205.mafya.api.exception;

import com.a205.mafya.api.response.BasicRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.NoSuchElementException;


@ControllerAdvice
@Slf4j
public class ExceptionHandler {

    // 이미 존재하는 userCode로 학생추가할때
    @org.springframework.web.bind.annotation.ExceptionHandler(UserCodeOverlapException.class)
    public ResponseEntity<BasicRes> UserCodeOverlapException(UserCodeOverlapException e) {
        log.error("UserCodeOverlapException", e);
        log.error(e.getMessage());
        log.error(e.getLocalizedMessage());

        BasicRes er = BasicRes.builder()
                .msg(e.getLocalizedMessage())
                // 1 : 중복 학번 있음
                .resultCode(1)
                .build();

        return new ResponseEntity<>(er, HttpStatus.OK);
    }

    // 존재하지 않는 유저 정보에 접근할때
    @org.springframework.web.bind.annotation.ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BasicRes> NoSuchElementException(NoSuchElementException e) {
        log.error("NoSuchElementException", e);
        log.error(e.getMessage());
        log.error(e.getLocalizedMessage());

        BasicRes er = BasicRes.builder()
                .msg(e.getLocalizedMessage())
                // 1 : 요청한 사용자 없음
                .resultCode(1)
                .build();

        return new ResponseEntity<>(er, HttpStatus.OK);
    }


    // 비밀번호 틀렸을때
    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BasicRes> BadCredentialsException(BadCredentialsException e) {
        log.error("BadCredentialsException", e);
        log.error(e.getMessage());
        log.error(e.getLocalizedMessage());

        BasicRes er = BasicRes.builder()
                .msg("Wrong Password, exception from ExceptionHandler.class")
                // 1 : 요청한 사용자 없음
                .resultCode(1)
                .build();

        return new ResponseEntity<>(er, HttpStatus.OK);
    }

}
