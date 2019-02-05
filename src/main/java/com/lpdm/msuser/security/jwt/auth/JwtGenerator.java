package com.lpdm.msuser.security.jwt.auth;

import com.lpdm.msuser.security.jwt.config.JwtAuthConfig;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtGenerator {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final JwtAuthConfig jwtConfig;

    /**
     * Generate the token with the jwtUser data and the secret signing key
     * @param jwtConfig The token config params
     */
    @Autowired
    public JwtGenerator(JwtAuthConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generate(JwtUser jwtUser){

        // Set the claims
        Claims claims = Jwts.claims().setSubject(jwtUser.getUserName());
        claims.put("id", jwtUser.getId());
        claims.put("role", jwtUser.getRole());

        // Build the token
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(jwtConfig.getExpiration())))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();

        // Return the correctly formatted JWT
        return jwtConfig.getPrefix() + " " + token;
    }
}
