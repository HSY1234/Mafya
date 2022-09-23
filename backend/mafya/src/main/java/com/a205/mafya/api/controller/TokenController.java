package com.a205.mafya.api.controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<?> reissue(@RequestHeader(value="accessToken") String accessToken, HttpServletRequest req, HttpServletResponse resp){

        // http only cookie에서 refresh token을 받아옵니다.
        String refreshToken = tokenProvider.resolveRefreshToken((req));
        String requestURI = req.getRequestURI();

        // ResponseEntity
        TokenExpRes ter = TokenExpRes.builder()
                // 1 : 유효하지 않은 접근
                .resultCode(1)
                .build();

        // 유효한 토큰인지 확인합니다.
        if (tokenProvider.validateToken(accessToken).equals("valid") && tokenProvider.validateToken(refreshToken).equals("valid")
        || tokenProvider.validateToken(accessToken).equals("expired") && tokenProvider.validateToken(refreshToken).equals("valid")) {
            log.debug("access 토큰이 만료되고 refresh 토큰은 유효하여 access/refresh 토큰을 모두 재발급합니다, uri: {}", requestURI);

            // 기존 refresh토큰은 유효하므로 이것으로 새로운 access, refresh 토큰을 발급한다.
            Authentication authentication = tokenProvider.getAuthentication(refreshToken);
            String newAccessToken = tokenProvider.createToken(authentication,'a');
            String newRefreshToken = tokenProvider.createToken(authentication, 'r');
            // accessToken이 만료되었기 때문에 accessToken와 refreshToken을 업데이트 해줌.
            // 응답에 새로 발급한 access token을 넣어준다.
            ter.changeMsg("your access token and refresh token are updated");
            ter.changeTokenStatus("update");
            ter.changeAccessToken(newAccessToken);
            // refreshToken은 HttpOnly cookie로 보낸다.
            cookieProvider.addTokenToCookie(resp, "refreshToken", newRefreshToken);

            log.debug("accessToken, refreshToken 재발급 완료");

        } else {
            ter.changeMsg("Your Tokens are invalid, so update denied");
            ter.changeTokenStatus("invalid");

            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        return new ResponseEntity<>(ter, HttpStatus.OK);
    }


}
