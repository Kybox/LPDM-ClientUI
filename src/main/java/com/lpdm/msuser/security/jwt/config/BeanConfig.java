package com.lpdm.msuser.security.jwt.config;

import com.lpdm.msuser.security.jwt.auth.JwtAuthProvider;
import com.lpdm.msuser.security.jwt.handler.JwtSuccessHandler;
import com.lpdm.msuser.security.jwt.auth.JwtValidator;
import com.lpdm.msuser.security.jwt.filter.JwtAuthTokenFilter;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Bean RequestMatcher customRequestMatch(){

        List<RequestMatcher> requestMatcherList = new ArrayList<>();
        requestMatcherList.add(new AntPathRequestMatcher("/shop/account/**"));
        requestMatcherList.add(new AntPathRequestMatcher("/shop/order/**"));
        requestMatcherList.add(new AntPathRequestMatcher("/admin"));
        requestMatcherList.add(new AntPathRequestMatcher("/admin/auth/**"));
        requestMatcherList.add(new AntPathRequestMatcher("/admin/products/**"));
        return new OrRequestMatcher(requestMatcherList);
    }

    @Bean
    public JwtAuthTokenFilter authenticationTokenFilter() {

        JwtAuthTokenFilter filter = new JwtAuthTokenFilter(customRequestMatch());
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
