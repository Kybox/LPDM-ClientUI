package com.lpdm.msuser.security.jwt.config;

import org.springframework.beans.factory.annotation.Value;

public class JwtAuthConfig {

    @Value("${lpdm.jwt.header}")
    private String header;

    @Value("${lpdm.jwt.prefix}")
    private String prefix;

    @Value("${lpdm.jwt.expiration}")
    private int expiration;

    @Value("${lpdm.jwt.secret}")
    private String secret;

    public JwtAuthConfig() {
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
