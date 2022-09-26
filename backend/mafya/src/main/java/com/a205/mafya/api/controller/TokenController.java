package com.a205.mafya.api.controller;

import com.a205.mafya.api.filter.exception.TokenException;
import com.a205.mafya.api.request.LoginReq;
import com.a205.mafya.api.response.LoginRes;
import com.a205.mafya.api.response.TokenExpRes;
import com.a205.mafya.api.service.AuthService;
import com.a205.mafya.api.service.UserService;
import com.a205.mafya.db.repository.UserRepository;
import com.a205.mafya.util.CookieProvider;
import com.a205.mafya.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    CookieProvider cookieProvider;

    // 로그인
    @GetMapping("reissue")
    public ResponseEntity<?> reissue(HttpServletRequest req, HttpServletResponse resp) throws TokenException {

        // http only cookie에서 refresh token을 받아옵니다.
        String refreshToken = tokenProvider.resolveRefreshToken((req));
        String requestURI = req.getRequestURI();

        // ResponseEntity
        TokenExpRes ter = TokenExpRes.builder()
                // 1 : 유효하지 않은 접근
                .resultCode(1)
                .build();

        // 유효한 토큰인지 확인합니다.
        if (tokenProvider.validateToken(refreshToken).equals("valid")) {
            log.debug("access 토큰이 만료되고 refresh 토큰은 유효하여 access/refresh 토큰을 모두 재발급합니다, uri: {}", requestURI);

            // 기존 refresh토큰은 유효하므로 이것으로 새로운 access, refresh 토큰을 발급한다.
            String newAccessToken = tokenProvider.createToken(tokenProvider.getAuthentication(refreshToken),'a');
            String newRefreshToken = tokenProvider.createToken(tokenProvider.getAuthentication(refreshToken),'r');
            // accessToken이 만료되었기 때문에 accessToken와 refreshToken을 업데이트 해줌.
            // 응답에 새로 발급한 access token을 넣어준다.
            ter.changeMsg("your access token and refresh token are updated");
            ter.changeTokenStatus("update");
            ter.changeAccessToken(newAccessToken);
            // refreshToken은 HttpOnly cookie로 보낸다.
            cookieProvider.addTokenToCookie(resp, "refreshToken", newRefreshToken);

            log.debug("accessToken, refreshToken 재발급 완료");
        // 유효하지 않은 토큰
        } else if(tokenProvider.validateToken(refreshToken).equals("expired")) {
            throw new TokenException("refresh tokens expired, update token denied",2,"expiredAR",refreshToken);
        }
        else {

            log.debug("업데이트 가능한 토큰이 아닙니다, uri: {}", requestURI);
            throw new TokenException("invalid tokens, update tokens denied",3,"invalid",refreshToken);

        }

        return new ResponseEntity<>(ter, HttpStatus.OK);
    }


}
