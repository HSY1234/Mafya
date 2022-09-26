package com.a205.mafya.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CookieProvider {

    public void addTokenToCookie(HttpServletResponse resp, String tokenName, String token) {
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setPath("/");
//        cookie.setSecure(true);
        cookie.setMaxAge(60 * 60 * 24 * 14); // 유효기간 2주
        // httpOnly 옵션을 추가해 서버만 쿠키에 접근할 수 있게 설정
        cookie.setHttpOnly(true);
//        cookie.setDomain("http://localhost:3000");
        resp.addCookie(cookie);
        addSameSite(resp, "none");
    }
    
    private void addSameSite(HttpServletResponse response, String sameSite) {
        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        for(String header : headers) {
            if(firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=" + sameSite));
                firstHeader = false;
                continue;
            }
            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=" + sameSite));
        }

    }
}
