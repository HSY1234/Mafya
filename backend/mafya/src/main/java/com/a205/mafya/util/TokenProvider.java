package com.a205.mafya.util;


import com.a205.mafya.db.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {


    private final UserRepository userRepository;




    @Value("${jwt.secret}")
    private String secret;

    // access토큰 유효시간 1시간
    private Long accessTokenValidityInMilliseconds=60*60*1000L;
    // refresh토큰 유효시간 2주
    private Long refreshTokenValidityInMilliseconds=14*24*60*60*1000L;


    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String userCode, char type) {
        Claims claims = Jwts.claims().setSubject(userCode); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Date now = new Date();
        Long expTime = 0L;
        switch (type){
            case 'a':
                expTime = accessTokenValidityInMilliseconds;
                break;
            case 'r':
                expTime = refreshTokenValidityInMilliseconds;
                break;

        }
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + expTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secret)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }



    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

//    // token에 담겨있는 정보를 이용해 Authentication 객체를 리턴하는 메소드 생성
//    public Authentication getAuthentication(String token) {
//        UserDetails userDetails = userDetailService.loadUserByUsername(this.getUserPk(token));
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

    // 토큰의 유효성 + 만료일자 확인

    public String validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return "valid";
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            return "malformed";
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return "expired";
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            return "unsupported";
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            return "illegal";
        }
    }


    // Request의 Header에서 token 값을 가져옵니다. "accessToken" : "TOKEN값'
    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader("accessToken");
    }
    public String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String key = "refreshToken";
        String value = "";
        if(cookies != null){
            for(int i=0;i<cookies.length;i++){
                if(key.equals(cookies[i].getName())){
                    value = cookies[i].getValue();
                    break;
                }
            }
        }
        return value;
    }


}