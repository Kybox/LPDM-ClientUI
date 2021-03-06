package com.lpdm.msuser.security.jwt.auth;

import com.lpdm.msuser.security.jwt.config.JwtAuthConfig;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.lpdm.msuser.utils.global.ValueType.ROLE_LIST;

@Component
public class JwtValidator {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private JwtAuthConfig jwtAuthConfig;

    @Autowired
    public JwtValidator(JwtAuthConfig jwtAuthConfig) {
        this.jwtAuthConfig = jwtAuthConfig;
    }

    /**
     * This method checks the JWT integrity with the signing key retrieves user data
     * @param token The JWT config params
     */
    public JwtUser validate(String token) {

        JwtUser jwtUser = null;

        // Remove the token prefix
        token = token.replace(jwtAuthConfig.getPrefix() + " ", "");

        try{

            // Parse the claims with the signing key
            Claims tokenBody = Jwts.parser()
                    .setSigningKey(jwtAuthConfig.getSecret())
                    .parseClaimsJws(token).getBody();

            // Build the jwtUser with the token data
            jwtUser = new JwtUser();
            jwtUser.setUserName(tokenBody.getSubject());
            jwtUser.setId((Integer) tokenBody.get("id"));
            jwtUser.setActive((Boolean) tokenBody.get("active"));

            List<String> roleList = Arrays
                    .stream(tokenBody.get(ROLE_LIST).toString().split(","))
                    .collect(Collectors.toList());

            jwtUser.setRoleList(roleList);
        }
        catch (Exception e) {

            // Thrown if the token is corrupt
            log.warn(e.getMessage());
        }

        return jwtUser;
    }
}
