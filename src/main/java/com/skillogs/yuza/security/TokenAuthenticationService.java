package com.skillogs.yuza.security;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenAuthenticationService {

    Authentication getAuthentication(HttpServletRequest req);

}