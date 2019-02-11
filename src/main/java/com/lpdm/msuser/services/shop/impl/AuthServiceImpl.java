package com.lpdm.msuser.services.shop.impl;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.shop.LoginForm;
import com.lpdm.msuser.proxy.AuthProxy;
import com.lpdm.msuser.services.shop.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthProxy authProxy;

    @Autowired
    public AuthServiceImpl(AuthProxy authProxy) {
        this.authProxy = authProxy;
    }

    @Override
    public User findUserById(int id) {

        return authProxy.findById(id);
    }

    @Override
    public User loginUser(LoginForm loginForm) {

        return authProxy.loginUser(loginForm);
    }

    @Override
    public User updateUser(User user) {

        return authProxy.updateUser(user);
    }
}
