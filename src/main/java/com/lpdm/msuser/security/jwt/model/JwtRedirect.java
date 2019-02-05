package com.lpdm.msuser.security.jwt.model;

public class JwtRedirect {

    private String path;

    public JwtRedirect(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "JwtRedirect{" +
                "path='" + path + '\'' +
                '}';
    }
}
