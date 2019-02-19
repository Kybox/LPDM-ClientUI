package com.lpdm.msuser.security.jwt.config;

import com.lpdm.msuser.security.jwt.filter.JwtAuthTokenFilter;
import com.lpdm.msuser.security.jwt.handler.JwtAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter{

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;

    @Autowired
    public JwtSecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint,
                             JwtAuthTokenFilter jwtAuthTokenFilter) {

        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
    }

    /**
     * The main method of security configuration in which specify the URLs that require authentication
     * @param http A HttpSecurity self injected
     * @throws Exception Thrown if something fail
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/producer/**").hasAuthority("PRODUCER")
                .antMatchers("/shop/account/**", "/shop/order/**").hasAuthority("CONSUMER");

        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthEntryPoint);

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/shop/login/**", "/shop/products/**");
    }
}
