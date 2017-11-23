package com.skillogs.yuza.net.validator;

import com.skillogs.yuza.net.dto.UserDto;
import com.skillogs.yuza.net.exception.ValidationException;
import com.skillogs.yuza.net.exception.ValidatorError;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component("userValidator")
public class UserValidator implements Validator<UserDto> {

    @Override
    public void validate(UserDto user) throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();

        if (StringUtils.isEmpty(user.getFirstName())) {
            errors.add(new ValidatorError("firstName", "NotEmpty"));
        }
        if (StringUtils.isEmpty(user.getLastName())) {
            errors.add(new ValidatorError("lastName", "NotEmpty"));
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            errors.add(new ValidatorError("email", "NotEmpty"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
