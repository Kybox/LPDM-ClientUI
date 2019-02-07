package com.lpdm.msuser.services.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.shop.LoginForm;

public interface AuthService {

    User findUserById(int id);

    User loginUser(LoginForm loginForm);
}
