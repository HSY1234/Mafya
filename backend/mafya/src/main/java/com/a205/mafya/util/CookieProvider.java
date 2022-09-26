package com.a205.mafya.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class CookieProvider {

    public void addTokenToCookie(HttpServletResponse resp, String tokenName, String token) {
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(60 * 60 * 24 * 14); // 유효기간 2주
        // httpOnly 옵션을 추가해 서버만 쿠키에 접근할 수 있게 설정
        cookie.setHttpOnly(true);
        cookie.setDomain("http://localhost:3000");
        resp.addCookie(cookie);
    }
}
