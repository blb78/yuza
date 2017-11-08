package com.skillogs.yuza.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class StatelessAuthenticationFilter extends GenericFilterBean {

    private final TokenAuthenticationService authenticationService;

    @Autowired
    public StatelessAuthenticationFilter(TokenAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getMethod().equals("POST") && httpRequest.getRequestURI().contains("/users/authenticate")){
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = authenticationService.getAuthentication(httpRequest);
        if (authentication == null){
            HttpServletResponse r = (HttpServletResponse) response;
            r.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}