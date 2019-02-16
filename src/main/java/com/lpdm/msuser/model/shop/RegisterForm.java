package com.lpdm.msuser.model.shop;

import com.lpdm.msuser.model.auth.UserRole;

import javax.validation.constraints.NotNull;

public class RegisterForm {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private UserRole role;

    public RegisterForm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "RegisterForm{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
