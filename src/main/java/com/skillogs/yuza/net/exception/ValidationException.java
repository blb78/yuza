package com.skillogs.yuza.net.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<ValidatorError> errors;

    public ValidationException(List<ValidatorError> errors) {
        this.errors = errors;
    }

    public List<ValidatorError> getErrors() {
        return errors;
    }
}
