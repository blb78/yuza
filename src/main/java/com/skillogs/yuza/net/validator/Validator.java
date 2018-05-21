package com.skillogs.yuza.net.validator;

import com.skillogs.yuza.net.exception.ValidationException;

public interface Validator<T> {
    void validate(T t) throws ValidationException;
}
