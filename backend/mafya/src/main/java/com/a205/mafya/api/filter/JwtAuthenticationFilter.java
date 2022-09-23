package com.a205.mafya.api.filter;

import com.a205.mafya.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//해당 클래스는 JwtTokenProvider가 검증을 끝낸 Jwt로부터 유저 정보를 조회해와서 UserPasswordAuthenticationFilter 로 전달합니다.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse= (HttpServletResponse) response;

        // 헤더에서 JWT 를 받아옵니다.
        String accessToken = tokenProvider.resolveAccessToken((HttpServletRequest) request);
        String refreshToken = tokenProvider.resolveRefreshToken((HttpServletRequest) request);
        String requestURI = httpServletRequest.getRequestURI();

        // 유효한 토큰인지 확인합니다.
        if(accessToken == null || refreshToken == null){
            log.debug("JWT 토큰이 없습니다, uri: {}", requestURI);
        }
        else if (tokenProvider.validateToken(accessToken).equals("valid") && tokenProvider.validateToken(refreshToken).equals("valid")) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 토큰상태가 valid함을 알려줌
            httpServletResponse.addHeader("status", "valid");
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        }
        // refresh토큰은 정상이고 access토큰은 만료된 경우
        else if (tokenProvider.validateToken(accessToken).equals("expired") && tokenProvider.validateToken(refreshToken).equals("valid")) {
            // 새로운 토큰을 발급한다.
            Authentication authentication = tokenProvider.getAuthentication(refreshToken);
            // accessToken이 만료되었기 때문에 accessToken와 refreshToken을 업데이트 해줌.
            httpServletResponse.addHeader("status", "updateAccessToken");
            httpServletResponse.addHeader("accessToken", tokenProvider.createToken(authentication,'a'));
            // refreshToken은 HttpOnly cookie로 보내기
            Cookie cookie = new Cookie("refreshToken", tokenProvider.createToken(authentication, 'r'));
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 1); // 유효기간 1일
            // httpOnly 옵션을 추가해 서버만 쿠키에 접근할 수 있게 설정
            cookie.setHttpOnly(true);
            httpServletResponse.addCookie(cookie);
            log.debug("accessToken, refreshToken 재발급 완료");

        }
        else {
            httpServletResponse.addHeader("status", "invalid");
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }
        chain.doFilter(request, response);
    }
}