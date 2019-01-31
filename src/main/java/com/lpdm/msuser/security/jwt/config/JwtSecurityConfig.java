package com.lpdm.msuser.security.jwt.config;

import com.lpdm.msuser.security.jwt.handler.JwtAuthEntryPoint;
import com.lpdm.msuser.security.jwt.filter.JwtAuthTokenFilter;
import com.lpdm.msuser.security.jwt.handler.JwtDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final JwtDeniedHandler jwtDeniedHandler;

    @Autowired
    public JwtSecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint,
                             JwtAuthTokenFilter jwtAuthTokenFilter, JwtDeniedHandler jwtDeniedHandler) {

        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
        this.jwtDeniedHandler = jwtDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.authorizeRequests().antMatchers("/admin/**").authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers().cacheControl();

    }
}