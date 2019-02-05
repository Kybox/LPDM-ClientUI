package com.lpdm.msuser.security.jwt.config;

import com.lpdm.msuser.security.jwt.auth.JwtAuthProvider;
import com.lpdm.msuser.security.jwt.handler.JwtSuccessHandler;
import com.lpdm.msuser.security.jwt.auth.JwtValidator;
import com.lpdm.msuser.security.jwt.filter.JwtAuthTokenFilter;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import java.util.Collections;

@Configuration
public class BeanConfig {

    @Bean
    public JwtAuthConfig jwtAuthConfig(){
        return new JwtAuthConfig();
    }

    @Bean
    public JwtValidator jwtValidator(){
        return new JwtValidator(jwtAuthConfig());
    }

    @Bean
    public JwtAuthProvider jwtAuthProvider(){
        return new JwtAuthProvider(jwtValidator());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(jwtAuthProvider()));
    }

    @Bean
    public JwtAuthTokenFilter authenticationTokenFilter() {
        JwtAuthTokenFilter filter = new JwtAuthTokenFilter(jwtAuthConfig());
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new JwtSuccessHandler());
        return filter;
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return (serverFactory) -> serverFactory.addContextCustomizers(
                (context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
    }
}
