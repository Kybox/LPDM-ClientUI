package com.lpdm.msuser.security.jwt.auth;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.auth.UserRole;
import com.lpdm.msuser.security.jwt.model.JwtUser;

public class JwtUserBuilder {

    /**
     * This method allows you to build JwUser object from the user data
     */
    public static JwtUser build(User user){

        JwtUser jwtUser = new JwtUser();
        jwtUser.setId(user.getId());
        jwtUser.setUserName(user.getFirstName());
        jwtUser.setActive(user.getActive());

        for(UserRole role : user.getAppRole())
            jwtUser.getRoleList().add(role.getRoleName());

        return jwtUser;
    }
}
