package com.a205.mafya.api.filter.exception;

import com.a205.mafya.api.response.BasicRes;
import com.a205.mafya.api.response.TokenExpRes;
import com.a205.mafya.util.CookieProvider;
import com.a205.mafya.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;



@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandler {

    private final TokenProvider tokenProvider;
    private final CookieProvider cookieProvider;

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
    @org.springframework.web.bind.annotation.ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<BasicRes> WrongPasswordException(WrongPasswordException e) {
        log.error("WrongPasswordException", e);
        log.error(e.getMessage());
        log.error(e.getLocalizedMessage());

        BasicRes er = BasicRes.builder()
                .msg("Wrong Password, exception from ExceptionHandler.class")
                // 1 : 요청한 사용자 없음
                .resultCode(1)
                .build();

        return new ResponseEntity<>(er, HttpStatus.OK);
    }


    // jwt 토큰이 유효하지 않을때
    @org.springframework.web.bind.annotation.ExceptionHandler(TokenException.class)
    public ResponseEntity<String> TokenException(TokenException e, HttpServletResponse resp) throws IOException {
        // log
        log.error("TokenException", e);
        log.error("Message : "+e.getMessage());
        log.error("Token Status : "+e.getTokenStatus());

//        // ResponseEntity
//        TokenExpRes ter = TokenExpRes.builder()
//                // 1 : 유효하지 않은 접근
//                .resultCode(e.getResultCode())
//                .tokenStatus(e.getTokenStatus())
//                .build();

        // accessToken은 만료이고 refreshToken은 유효한 경우
        if(e.getTokenStatus().equals("expiredAccess")){

            resp.sendError(HttpStatus.REQUEST_TIMEOUT.value(),"expired access token and valid refresh token");
//            // 기존 refresh토큰은 유효하므로 이것으로 새로운 access, refresh 토큰을 발급한다.
//            String userCode = tokenProvider.getUserPk(e.getOldRefreshToken());
//            String newAccessToken = tokenProvider.createToken(userCode,'a');
//            String newRefreshToken = tokenProvider.createToken(userCode, 'r');
//            // accessToken이 만료되었기 때문에 accessToken와 refreshToken을 업데이트 해줌.
//            // 응답에 새로 발급한 access token을 넣어준다.
//            ter.changeMsg(e.getMessage());
//            ter.changeAccessToken(newAccessToken);
//            // refreshToken은 HttpOnly cookie로 보낸다.
//            cookieProvider.addTokenToCookie(res, "refreshToken", newRefreshToken);
//
//            log.debug("accessToken, refreshToken 재발급 완료");
            // token이 유효하지 않은 경우
        }else if(e.getTokenStatus().equals("expiredAR")){

            resp.sendError(HttpStatus.NOT_ACCEPTABLE.value(),"expired access token and expired refresh token");
//            ter.changeMsg(e.getMessage());
        }else{
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,"invalid tokens");
        }

        return new ResponseEntity<String>("token error", HttpStatus.OK);
    }

}
