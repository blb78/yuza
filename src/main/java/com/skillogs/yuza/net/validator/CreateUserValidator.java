package com.skillogs.yuza.net.validator;

import com.skillogs.yuza.domain.Role;
import com.skillogs.yuza.net.dto.UserDto;
import com.skillogs.yuza.net.exception.ApiBadRequestException;
import com.skillogs.yuza.net.exception.ApiConflictException;
import com.skillogs.yuza.net.exception.ValidationException;
import com.skillogs.yuza.net.exception.ValidatorError;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component("createUserValidator")
public class CreateUserValidator implements Validator<UserDto> {

    private final UserValidator validator;
    private final UserRepository repository;
    private final Pattern emailPattern;

    public CreateUserValidator(@Qualifier("userValidator") UserValidator validator,
                               UserRepository repository) {
        this.validator = validator;
        this.repository = repository;
        this.emailPattern = Pattern.compile("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$");
    }

    @Override
    public void validate(UserDto user) throws ValidationException {
        List<ValidatorError> errors = new ArrayList<>();

        try {
            validator.validate(user);
        } catch (ValidationException ex) {
            errors.addAll(ex.getErrors());
        }

        if (StringUtils.isEmpty(user.getPassword())) {
             errors.add(new ValidatorError("password", "NotEmpty"));
        }

        if (CollectionUtils.isEmpty(user.getRoles())) {
            errors.add(new ValidatorError("roles", "NotEmpty"));
        }

        if (!emailPattern.matcher(user.getEmail()).matches()) {
            errors.add(new ValidatorError("email", "Email"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        if (!areValid(user.getRoles())){
            throw new ApiBadRequestException();
        }
        if (repository.countByEmail(user.getEmail())>0) {
            throw new ApiConflictException();
        }

    }

    private boolean areValid(Set<String> roles) {
        List<String> allRoles = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList());
        return allRoles.containsAll(roles);
    }
}
