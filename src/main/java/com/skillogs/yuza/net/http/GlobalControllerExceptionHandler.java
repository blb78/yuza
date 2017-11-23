package com.skillogs.yuza.net.http;

import com.skillogs.yuza.net.exception.ApiBadRequestException;
import com.skillogs.yuza.net.exception.ApiConflictException;
import com.skillogs.yuza.net.exception.ApiCourseNotFoundException;
import com.skillogs.yuza.net.exception.ApiNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(ApiConflictException.class)
    @ResponseStatus(value= HttpStatus.CONFLICT, reason="Email already exist")
    public void ApiEmailAlreadyExistException()  {  }

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="User not found")
    public void ApiNotFoundException()  {  }

    @ExceptionHandler(ApiCourseNotFoundException.class)
    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Course not found")
    public void ApiCourseNotFoundException()  {  }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value= HttpStatus.UNAUTHORIZED)
    public void AuthenticationException()  {  }

    @ExceptionHandler(ApiBadRequestException.class)
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    public void BadRequestException()  {  }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value= HttpStatus.FORBIDDEN)
    public void AccessDeniedException()  {  }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> HandleMethodArgumentNotValidException(MethodArgumentNotValidException exception)  {
        return ResponseEntity.badRequest().body(exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getCode()))
                .collect(Collectors.toList()));
    }


    static class ValidationError {
        String message;
        String field;

        public ValidationError(String field, String message) {
            this.message = message;
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }



}
