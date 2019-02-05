package com.lpdm.msuser.model.auth;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesBean {

    private int id;

    private UserRole appRole;

    private User appUser;

}
