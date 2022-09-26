package com.a205.mafya.config;

import com.a205.mafya.api.filter.JwtAuthenticationFilter;
import com.a205.mafya.util.CookieProvider;
import com.a205.mafya.util.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsUtils;


@EnableWebSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CookieProvider cookieProvider;
    private final ObjectMapper objectMapper;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 인증 범위를 설정
                .and()
                .authorizeRequests()
                // Preflight request의 예비요청이 security에 의해 막히는 것을 방지하기 위함
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                //permitAll, permitAll이 authenticated보다 먼저와야한다.
//                .anyRequest().permitAll()


                .antMatchers(HttpMethod.POST, "/student").permitAll()
                .antMatchers(HttpMethod.POST, "/manager").permitAll()
                .antMatchers("/student/login").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()


                //authenticated
                .antMatchers("/student/**").authenticated()
                .antMatchers("/manager/**").authenticated()




                // jwt 필터 적용
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, cookieProvider, objectMapper),
                        UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.disable());


        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> {
//            web.ignoring()
//                    .antMatchers(HttpMethod.POST,
//                            "/student/login"
//                    )
//                    // swagger 관련 접근에 대해 filtering 하지 않도록 한다.
//                    .antMatchers(
//                            "/swagger-ui.html",
//                            "/webjars/**",
//                            "/null/**",
//                            "/swagger-resources/**",
//                            "/v2/**",
//                            "/csrf/**"
////                            "/"
//
//                    )
//            ;
//        };
//    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

}

