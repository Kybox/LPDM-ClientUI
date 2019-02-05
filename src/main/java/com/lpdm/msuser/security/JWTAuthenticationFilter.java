
package com.lpdm.msuser.security;
/*

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpdm.msuser.msauthentication.AppUserBean;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AppUserBean appUserBean = null;
        try {
            appUserBean = new ObjectMapper().readValue(request.getInputStream(), AppUserBean.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("*************");
        System.out.println("username :" + appUserBean.getEmail());
        System.out.println("password :" + appUserBean.getPassword());
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(appUserBean.getEmail(), appUserBean.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        System.out.println("Entrée dans successfulAuthentication");
        User springUser = (User)authResult.getPrincipal();

        String jwtToken = Jwts.builder()
                .setSubject(springUser.getUserName())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET)
                .claim("roles", springUser.getAuthorities())
                .compact();
        System.out.println("JWT token: " + jwtToken);
        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX +jwtToken);
    }
}

 */