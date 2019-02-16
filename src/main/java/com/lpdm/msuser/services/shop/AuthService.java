package com.lpdm.msuser.services.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.shop.LoginForm;
import com.lpdm.msuser.model.shop.RegisterForm;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    User findUserById(int id);
    User loginUser(LoginForm loginForm);
    User registerUser(RegisterForm registerForm);
    User updateUser(User user);
}
