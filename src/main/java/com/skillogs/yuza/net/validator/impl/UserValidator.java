package com.skillogs.yuza.net.validator.impl;

import com.skillogs.yuza.domain.Role;
import com.skillogs.yuza.net.dto.UserDto;
import com.skillogs.yuza.net.exception.ValidationException;
import com.skillogs.yuza.net.exception.ValidatorError;
import com.skillogs.yuza.net.validator.Validator;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class UserValidator implements Validator<UserDto> {

    private final UserRepository repository;
    private final Pattern emailPattern;


    public UserValidator(UserRepository repository) {
        this.repository = repository;
        this.emailPattern = Pattern.compile("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$");
    }

    @Override
    public void validate(UserDto user) throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();
        if (user.getId() == null) {
            if (StringUtils.isEmpty(user.getPassword())) {
                errors.add(new ValidatorError("password", "NotEmpty"));
            }
            if (CollectionUtils.isEmpty(user.getRoles())) {
                errors.add(new ValidatorError("roles", "NotEmpty"));
            }
            if (!areValid(user.getRoles())){
                errors.add(new ValidatorError("roles", "Invalid"));
            }
            if (repository.countByEmail(user.getEmail()) > 0) {
                errors.add(new ValidatorError("email", "AlreadyUsed"));
            }
        }

        if (StringUtils.isEmpty(user.getEmail())) {
            errors.add(new ValidatorError("email", "NotEmpty"));
        } else if (!emailPattern.matcher(user.getEmail()).matches()) {
            errors.add(new ValidatorError("email", "EmailPattern"));
        }
        if (StringUtils.isEmpty(user.getFirstName())) {
            errors.add(new ValidatorError("firstName", "NotEmpty"));
        }
        if (StringUtils.isEmpty(user.getLastName())) {
            errors.add(new ValidatorError("lastName", "NotEmpty"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean areValid(Set<String> roles) {
        List<String> allRoles = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList());
        return allRoles.containsAll(roles);
    }
}
