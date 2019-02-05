package com.lpdm.msuser.security.jwt.filter;

import com.lpdm.msuser.security.cookie.CookieRemover;
import com.lpdm.msuser.security.jwt.config.JwtAuthConfig;
import com.lpdm.msuser.security.jwt.model.JwtAuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import static com.lpdm.msuser.utils.shop.ValueType.USER_ACCOUNT_LOCKED;


public class JwtAuthTokenFilter extends AbstractAuthenticationProcessingFilter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final JwtAuthConfig jwtConfig;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtAuthTokenFilter(JwtAuthConfig jwtConfig) {

        super("/admin/**");
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        log.warn("Authentication failed -> " + failed.getMessage());

        CookieRemover.remove(response);

        if(failed.getMessage().equals(USER_ACCOUNT_LOCKED))
            response.sendRedirect("/identification/login?error=" + URLEncoder.encode("Compte utilisateur désactivé", "UTF-8"));
        else  response.sendRedirect("/identification/login?error=" + failed.getMessage());

    }

    /**
     * This method filters cookies to retrieve a token and validate the authentication.
     * Otherwise a redirection to the login page is set in the HttpServletResponse.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)

            throws AuthenticationException, IOException, ServletException {

        String jwtCookie = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for(Cookie cookie : cookies){
                if (cookie.getName().equals(jwtConfig.getHeader())){
                    jwtCookie = cookie.getValue();
                    break;
                }
            }
        }

        if(jwtCookie == null || !jwtCookie.startsWith(jwtConfig.getPrefix())) {
            log.warn("JWT Token is missing !");
            log.info("PathInfo" + request.getPathInfo());
            log.info("PathTranslated" + request.getPathTranslated());
            log.info("RequestURL" + request.getRequestURL().toString());

            String urlRequest = request.getRequestURL().toString();
            String param = urlRequest.substring(urlRequest.lastIndexOf("/") + 1);
            log.info("Param = " + param);

            response.sendRedirect("/identification/login?page=" + param);
            return null;
        }

        // Remove the prefix from the token
        String token = jwtCookie.replace(jwtConfig.getPrefix() + " ", "");

        JwtAuthToken jwtAuthToken = new JwtAuthToken(token);

        return authenticationManager.authenticate(jwtAuthToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth)

            throws IOException, ServletException {

        super.successfulAuthentication(request, response, chain, auth);

        log.info("Successful Authentication method");

        chain.doFilter(request, response);
    }
}
