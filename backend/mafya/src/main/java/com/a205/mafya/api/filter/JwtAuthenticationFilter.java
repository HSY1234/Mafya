package com.a205.mafya.api.filter;

import com.a205.mafya.api.exception.TokenException;
import com.a205.mafya.api.response.TokenExpRes;
import com.a205.mafya.util.CookieProvider;
import com.a205.mafya.util.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//해당 클래스는 JwtTokenProvider가 검증을 끝낸 Jwt로부터 유저 정보를 조회해와서 UserPasswordAuthenticationFilter 로 전달합니다.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;
    private final CookieProvider cookieProvider;

    private final ObjectMapper objectMapper;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // 헤더에서 JWT 를 받아옵니다.
        String accessToken = tokenProvider.resolveAccessToken((HttpServletRequest) request);
        String refreshToken = tokenProvider.resolveRefreshToken((HttpServletRequest) request);
        String requestURI = httpServletRequest.getRequestURI();

        // ResponseEntity
        TokenExpRes ter = TokenExpRes.builder()
                // 1 : 유효하지 않은 접근
                .resultCode(1)
                .build();

        System.out.println("RequestURI : "+httpServletRequest.getRequestURI());

        // 유효한 토큰인지 확인합니다.
        if (tokenProvider.validateToken(accessToken).equals("valid") && tokenProvider.validateToken(refreshToken).equals("valid")) {
            log.debug("A");
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        }
        // refresh토큰은 정상이고 access토큰은 만료된 경우
        else if (tokenProvider.validateToken(accessToken).equals("expired") && tokenProvider.validateToken(refreshToken).equals("valid")) {
            log.debug("access 토큰이 만료되고 refresh 토큰은 유효하여 access/refresh 토큰을 모두 재발급합니다, uri: {}", requestURI);
//            throw new TokenException("refresh token is valid, but access token expired. " +
//                    "Please Update your access Token by following access token.",1,"expired",refreshToken);
            // 기존 refresh토큰은 유효하므로 이것으로 새로운 access, refresh 토큰을 발급한다.
            Authentication authentication = tokenProvider.getAuthentication(refreshToken);
            String newAccessToken = tokenProvider.createToken(authentication,'a');
            String newRefreshToken = tokenProvider.createToken(authentication, 'r');
            // accessToken이 만료되었기 때문에 accessToken와 refreshToken을 업데이트 해줌.
            // 응답에 새로 발급한 access token을 넣어준다.
            ter.changeMsg("refresh token is valid, but access token expired. " +
                    "Please Update your access Token by following access token.");
            ter.changeTokenStatus("expired");
            ter.changeAccessToken(newAccessToken);
            // refreshToken은 HttpOnly cookie로 보낸다.
            cookieProvider.addTokenToCookie(httpServletResponse, "refreshToken", newRefreshToken);
            // 처리 결과를 Response에 알려준다.
            setTERToResp(ter, httpServletResponse);


            log.debug("accessToken, refreshToken 재발급 완료");

        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
//            throw new TokenException("Your Tokens are invalid, so access denied", 1,"invalid",refreshToken);
            ter.changeMsg("Your Tokens are invalid, so access denied");
            ter.changeTokenStatus("invalid");
            // 처리 결과를 Response에 알려준다.
            setTERToResp(ter, httpServletResponse);
        }

        log.debug("B");

        chain.doFilter(request, response);
    }

    public void setTERToResp(TokenExpRes ter, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(objectMapper.writeValueAsString(ter));
    }
}