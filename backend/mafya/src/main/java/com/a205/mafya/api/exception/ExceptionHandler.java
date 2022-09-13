package com.a205.mafya.api.exception;

import com.a205.mafya.api.response.ExceptionRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
@Slf4j
public class ExceptionHandler {

    // 이미 존재하는 userCode로 학생추가할때
    @org.springframework.web.bind.annotation.ExceptionHandler(UserCodeOverlapException.class)
    public ResponseEntity<Integer> UserCodeOverlapException(UserCodeOverlapException e) {
        log.error("UserCodeOverlapException", e);
        log.error(e.getMessage());
        log.error(e.getLocalizedMessage());

        return new ResponseEntity<>(1, HttpStatus.OK);
    }

}
