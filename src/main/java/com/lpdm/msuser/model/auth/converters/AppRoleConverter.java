package com.lpdm.msuser.model.auth.converters;


import com.lpdm.msuser.model.auth.UserRole;
import com.lpdm.msuser.proxy.MsUserProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AppRoleConverter implements Converter<String, UserRole> {

    @Autowired
    MsUserProxy userProxy;

    @Override
    public UserRole convert(String roleId) {

        int id = Integer.parseInt(roleId);
        UserRole appRoleBean = userProxy.getRoleById(id);

        return appRoleBean;
    }
}
