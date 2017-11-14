package com.skillogs.yuza.config;

import com.skillogs.yuza.security.StatelessAuthenticationFilter;
import com.skillogs.yuza.security.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebAppSecurity extends WebSecurityConfigurerAdapter {

    private final TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public WebAppSecurity(TokenAuthenticationService tokenAuthenticationService) {
        super(true);
        this.tokenAuthenticationService =  tokenAuthenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable().authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // Custom Token based authentication based on the header previously given to the client
                .addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class)
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/users/authenticate");
    }

}