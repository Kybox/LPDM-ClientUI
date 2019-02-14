package com.lpdm.msuser.security.jwt.model;

import java.util.ArrayList;
import java.util.List;

public class JwtUser {

    private int id;
    private String userName;
    private boolean active;
    private List<String> roleList;

    public JwtUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getRoleList() {
        if(roleList == null) roleList = new ArrayList<>();
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    @Override
    public String toString() {
        return "JwtUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", active=" + active +
                ", roleList=" + roleList +
                '}';
    }
}
