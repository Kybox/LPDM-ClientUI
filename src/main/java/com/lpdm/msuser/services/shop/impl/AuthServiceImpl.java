package com.lpdm.msuser.services.shop.impl;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.auth.UserRole;
import com.lpdm.msuser.model.shop.LoginForm;
import com.lpdm.msuser.model.shop.RegisterForm;
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
    public User registerUser(RegisterForm registerForm) {

        User user = new User();
        if(registerForm.getEmail().isEmpty() || registerForm.getEmail() == null)
            throw new RuntimeException("L'adresse e-mail doit être conforme");
        else user.setEmail(registerForm.getEmail());

        if(registerForm.getName().isEmpty() || registerForm.getName() == null)
            throw new RuntimeException("Le nom de doit pas être vide");
        else user.setName(registerForm.getName());

        if(registerForm.getPassword().isEmpty() || registerForm.getPassword() == null)
            throw new RuntimeException("Le mot de passe ne doit pas être vide");
        else user.setPassword(registerForm.getPassword());

        user.getAppRole().add(new UserRole(4));

        return authProxy.addNewUser(user);
    }

    @Override
    public User updateUser(User user) {

        return authProxy.updateUser(user);
    }
}
