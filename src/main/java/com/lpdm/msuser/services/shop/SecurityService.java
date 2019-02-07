package com.lpdm.msuser.services.shop;

import com.lpdm.msuser.model.auth.User;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

    User getAuthenticatedUser(HttpServletRequest request);
}
