package com.lpdm.msuser.services.shop.impl;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.security.jwt.auth.JwtValidator;
import com.lpdm.msuser.security.jwt.config.JwtAuthConfig;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import com.lpdm.msuser.services.shop.SecurityService;
import com.lpdm.msuser.services.shop.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final JwtAuthConfig jwtAuthConfig;
    private final JwtValidator jwtValidator;
    private final AuthService userService;

    @Autowired
    public SecurityServiceImpl(JwtAuthConfig jwtAuthConfig,
                               JwtValidator jwtValidator,
                               AuthService userService) {

        this.jwtAuthConfig = jwtAuthConfig;
        this.jwtValidator = jwtValidator;
        this.userService = userService;
    }

    @Override
    public User getAuthenticatedUser(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;

        for (Cookie cookie : cookies){
            if (cookie.getName().equals(jwtAuthConfig.getHeader())){

                JwtUser jwtUser = jwtValidator.validate(cookie.getValue());
                if (jwtUser == null) return null;

                User user = userService.findUserById(jwtUser.getId());
                if(user == null) return null;
                else return user;
            }
        }

        return null;
    }
}
