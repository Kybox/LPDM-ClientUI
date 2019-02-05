package com.lpdm.msuser.security.jwt.auth;

import com.lpdm.msuser.security.jwt.model.JwtAuthToken;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import com.lpdm.msuser.security.jwt.model.JwtUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthProvider extends AbstractUserDetailsAuthenticationProvider {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final JwtValidator jwtValidator;

    @Autowired
    public JwtAuthProvider(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken userPassAuthToken)
            throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username,
                                       UsernamePasswordAuthenticationToken userPassAuthToken)

            throws AuthenticationException {

        JwtAuthToken jwtAuthToken = (JwtAuthToken) userPassAuthToken;
        String token = jwtAuthToken.getToken();

        // Check if the JWT is valide (with the secret signing key) and get the user data
        JwtUser jwtUser = jwtValidator.validate(token);

        if(jwtUser == null)
            throw new RuntimeException("Your token is not valid, do not play this game");

        // Get the roles from the jwtUser to the security authorities
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(jwtUser.getRole()));

        // Return the custom UserDetails implemented class
        return new JwtUserDetails(
                jwtUser.getId(),
                jwtUser.getUserName(),
                token, jwtUser.isActive(),
                grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> supportClass) {
        return (JwtAuthToken.class.isAssignableFrom(supportClass));
    }
}
