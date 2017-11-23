package com.skillogs.yuza.net.exception;

public class ValidatorError {
    private final String field;
    private final String message;

    public ValidatorError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
