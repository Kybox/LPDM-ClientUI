package com.lpdm.msuser.security.cookie;

import com.lpdm.msuser.security.jwt.config.JwtAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtCookieRemover {

    private static JwtAuthConfig jwtAuthConfig;

    @Autowired
    public JwtCookieRemover(JwtAuthConfig jwtAuthConfig) {
        JwtCookieRemover.jwtAuthConfig = jwtAuthConfig;
    }

    public static void remove(HttpServletResponse response){

        Cookie cookie = new Cookie(jwtAuthConfig.getHeader(), "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
