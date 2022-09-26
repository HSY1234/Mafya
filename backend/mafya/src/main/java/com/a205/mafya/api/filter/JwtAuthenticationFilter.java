package com.a205.mafya.api.filter;

import com.a205.mafya.util.CookieProvider;
import com.a205.mafya.util.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        HttpServletResponse httpServletResponse = (HttpServletResponse) request;

        // 헤더에서 JWT 를 받아옵니다.
        String token = tokenProvider.resolveRefreshToken((HttpServletRequest) request);
        String requestURI = httpServletRequest.getRequestURI();

        cookieProvider.addTokenToCookie(httpServletResponse,"refreshToken",token);

        // 유효한 토큰인지 확인합니다.
//        if (token != null && (tokenProvider.validateToken(token).equals("valid") || tokenProvider.validateToken(token).equals("expired"))) {
        if (token != null && (tokenProvider.validateToken(token).equals("valid") || tokenProvider.validateToken(token).equals("expired"))) {
            // 일단 리프레시가 유효하거나 만료하거나 하면 둘 다 받아주고 둘의 분기처리는 controller안에 token service로 한다.
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = tokenProvider.getAuthentication(token);
            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else if (token != null && (tokenProvider.validateToken(token).equals("malformed") || tokenProvider.validateToken(token).equals("unsupported") || tokenProvider.validateToken(token).equals("illegal"))) {
            httpServletResponse.sendError(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value(),"유효하지 않은 토큰입니다.");
        }else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        chain.doFilter(request, response);


//        // ResponseEntity
//        TokenExpRes ter = TokenExpRes.builder()
//                // 1 : 유효하지 않은 접근
//                .resultCode(1)
//                .build();
//
//        // 유효한 토큰인지 확인합니다.
//        if (tokenProvider.validateToken(accessToken).equals("valid") && tokenProvider.validateToken(refreshToken).equals("valid")) {
//            log.debug("A");
//            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
//            Authentication authentication = tokenProvider.getAuthentication(accessToken);
//            // SecurityContext 에 Authentication 객체를 저장합니다.
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
//        }
//        // refresh토큰은 정상이고 access토큰은 만료된 경우
//        else if (tokenProvider.validateToken(accessToken).equals("expired") && tokenProvider.validateToken(refreshToken).equals("valid")) {
//            log.debug("access 토큰이 만료되고 refresh 토큰은 유효하여 access/refresh 토큰을 모두 재발급합니다, uri: {}", requestURI);
////            throw new TokenException("refresh token is valid, but access token expired. " +
////                    "Please Update your access Token by following access token.",1,"expired",refreshToken);
//            // 기존 refresh토큰은 유효하므로 이것으로 새로운 access, refresh 토큰을 발급한다.
//            Authentication authentication = tokenProvider.getAuthentication(refreshToken);
//            String newAccessToken = tokenProvider.createToken(authentication,'a');
//            String newRefreshToken = tokenProvider.createToken(authentication, 'r');
//            // accessToken이 만료되었기 때문에 accessToken와 refreshToken을 업데이트 해줌.
//            // 응답에 새로 발급한 access token을 넣어준다.
//            ter.changeMsg("refresh token is valid, but access token expired. " +
//                    "Please Update your access Token by following access token.");
//            ter.changeTokenStatus("expired");
//            ter.changeAccessToken(newAccessToken);
//            // refreshToken은 HttpOnly cookie로 보낸다.
//            cookieProvider.addTokenToCookie(httpServletResponse, "refreshToken", newRefreshToken);
//            // 처리 결과를 Response에 알려준다.
//            setTERToResp(ter, httpServletResponse);
//
//
//            log.debug("accessToken, refreshToken 재발급 완료");
//
//        } else {
//            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
////            throw new TokenException("Your Tokens are invalid, so access denied", 1,"invalid",refreshToken);
//            ter.changeMsg("Your Tokens are invalid, so access denied");
//            ter.changeTokenStatus("invalid");
//            // 처리 결과를 Response에 알려준다.
//            setTERToResp(ter, httpServletResponse);
//        }

    }

//    public void setTERToResp(TokenExpRes ter, HttpServletResponse resp) throws IOException {
//        resp.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = resp.getWriter();
//        out.print(objectMapper.writeValueAsString(ter));
//    }
}