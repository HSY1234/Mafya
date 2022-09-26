package com.a205.mafya.config;

import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(3000);
    }

    // local의 주소를 서버의 tomcat이 인식 못하는 문제 해결
    // java.lang.IllegalArgumentException: An invalid domain [http://localhost:3000] was specified for this cookie
    @Bean
    public ServletWebServerFactory tomcatCustomizer() {
                TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
                tomcat.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
            return tomcat;
    }

}
