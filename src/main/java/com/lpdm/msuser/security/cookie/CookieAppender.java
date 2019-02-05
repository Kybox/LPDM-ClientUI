package com.lpdm.msuser.security.cookie;

import com.lpdm.msuser.security.jwt.config.JwtAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieAppender {

    private static JwtAuthConfig jwtAuthConfig;

    @Autowired
    public CookieAppender(JwtAuthConfig jwtAuthConfig) {
        CookieAppender.jwtAuthConfig = jwtAuthConfig;
    }

    /**
     * This method adds the token in HttpServletResponse header
     */
    public static void addToken(String token, HttpServletResponse response){

        Cookie cookie = new Cookie(jwtAuthConfig.getHeader(), token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
