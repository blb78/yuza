package com.skillogs.yuza.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid account is
 * found.
 */
public class JWTFilter extends GenericFilterBean {

    private final TokenAuthenticationService tokenProvider;

    public JWTFilter(TokenAuthenticationService tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        Authentication auth = this.tokenProvider.getAuthentication((HttpServletRequest) request);
        if (auth == null || !auth.isAuthenticated()) {
            HttpServletResponse r = (HttpServletResponse) response;
            r.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

}