package com.lpdm.msuser.security.jwt.auth;

import com.lpdm.msuser.msauthentication.AppUserBean;
import com.lpdm.msuser.security.jwt.model.JwtUser;

public class JwtUserBuilder {

    /**
     * This method allows you to build JwUser object from the user data
     */
    public static JwtUser build(AppUserBean user){

        JwtUser jwtUser = new JwtUser();
        jwtUser.setId(user.getId());
        jwtUser.setUserName(user.getFirstName());
        jwtUser.setRole(user.getAppRole().get(0).getRoleName());
        jwtUser.setActive(user.getActive());

        return jwtUser;
    }
}
