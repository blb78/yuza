package com.skillogs.yuza.net.http;

import com.skillogs.yuza.net.exception.ApiConflictException;
import com.skillogs.yuza.net.exception.ApiNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(ApiConflictException.class)
    @ResponseStatus(value= HttpStatus.CONFLICT, reason="Email already exist")
    public void ApiEmailAlreadyExistException()  {  }

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="User not found")
    public void ApiNotFoundException()  {  }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value= HttpStatus.UNAUTHORIZED)
    public void AuthenticationException()  {  }



}
