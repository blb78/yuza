package com.skillogs.yuza.net.validator.impl;

import com.skillogs.yuza.domain.Role;
import com.skillogs.yuza.net.dto.UserDto;
import com.skillogs.yuza.net.exception.ValidationException;
import com.skillogs.yuza.net.exception.ValidatorError;
import com.skillogs.yuza.net.validator.Validator;
import com.skillogs.yuza.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

import static com.skillogs.yuza.TestUtils.build;
import static com.skillogs.yuza.TestUtils.extract;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class UserValidatorTest {
    private UserRepository repository;
    private Validator<UserDto> validator;

    @Before
    public void setup() {
        this.repository = Mockito.mock(UserRepository.class);
        this.validator = new UserValidator(repository);
    }

    @After
    public void clean() {
        Mockito.reset(this.repository);
    }

    @Test
    public void failed_to_validate_new_empty_user() {
        UserDto dto = build(UserDto::new);
        try {
            validator.validate(dto);
            fail("Had to throw validation Exception, but got nothing");
        } catch (ValidationException ex) {
            List<ValidatorError> errors = ex.getErrors();
            assertThat(extract(errors, ValidatorError::getField), Matchers.containsInAnyOrder("email", "firstName", "lastName", "roles", "password"));
            assertThat(extract(errors, ValidatorError::getMessage), Matchers.containsInAnyOrder("NotEmpty", "NotEmpty", "NotEmpty", "NotEmpty", "NotEmpty"));
        }
    }

    @Test
    public void failed_to_validate_empty_user() {
        UserDto dto = build(UserDto::new, u-> u.setId("id"));
        try {
            validator.validate(dto);
            fail("Had to throw validation Exception, but got nothing");
        } catch (ValidationException ex) {
            List<ValidatorError> errors = ex.getErrors();
            assertThat(extract(errors, ValidatorError::getField), Matchers.containsInAnyOrder("email", "firstName", "lastName"));
            assertThat(extract(errors, ValidatorError::getMessage), Matchers.containsInAnyOrder("NotEmpty", "NotEmpty", "NotEmpty"));
        }
    }

    @Test
    public void failed_to_validate_bad_email_format() {
        UserDto dto = build(UserDto::new,
                u -> u.setId("id"),
                u -> u.setFirstName("john"),
                u -> u.setLastName("doe"),
                u -> u.addRole(Role.ADMIN.name()),
                u -> u.setEmail("bad email"));
        try {
            validator.validate(dto);
            fail("Had to throw validation Exception, but got nothing");
        } catch (ValidationException ex) {
            List<ValidatorError> errors = ex.getErrors();
            assertThat(extract(errors, ValidatorError::getField), Matchers.containsInAnyOrder("email"));
            assertThat(extract(errors, ValidatorError::getMessage), Matchers.containsInAnyOrder("EmailPattern"));
        }
    }

    @Test
    public void failed_to_validate_bad_role() {
        UserDto dto = build(UserDto::new,
                u -> u.setFirstName("john"),
                u -> u.setPassword("password"),
                u -> u.setLastName("doe"),
                u -> u.addRole(Role.ADMIN.name()),
                u -> u.addRole("WIZZARD"),
                u -> u.setEmail("email@server.com"));
        try {
            validator.validate(dto);
            fail("Had to throw validation Exception, but got nothing");
        } catch (ValidationException ex) {
            List<ValidatorError> errors = ex.getErrors();
            assertThat(extract(errors, ValidatorError::getField), Matchers.containsInAnyOrder("roles"));
            assertThat(extract(errors, ValidatorError::getMessage), Matchers.containsInAnyOrder("Invalid"));
        }
    }

    @Test
    public void failed_to_validate_already_used_mail_for_new_user() {
        UserDto dto = build(UserDto::new,
                u -> u.setFirstName("john"),
                u -> u.setLastName("doe"),
                u -> u.setPassword("password"),
                u -> u.addRole(Role.ADMIN.name()),
                u -> u.setEmail("already@used.email"));
        when(repository.countByEmail(dto.getEmail())).thenReturn(1L);

        try {
            validator.validate(dto);
            fail("Had to throw validation Exception, but got nothing");
        } catch (ValidationException ex) {
            List<ValidatorError> errors = ex.getErrors();
            assertThat(extract(errors, ValidatorError::getField), Matchers.containsInAnyOrder("email"));
            assertThat(extract(errors, ValidatorError::getMessage), Matchers.containsInAnyOrder("AlreadyUsed"));
        }
    }

    @Test
    public void should_validate_user_with_same_email() {
        UserDto dto = build(UserDto::new,
                u -> u.setId("id"),
                u -> u.setLastName("doe"),
                u -> u.setFirstName("john"),
                u -> u.setPassword("password"),
                u -> u.addRole(Role.ADMIN.name()),
                u -> u.setEmail("john@doe.email"));
        when(repository.countByEmail(dto.getEmail())).thenReturn(1L);

        try {
            validator.validate(dto);
        } catch (ValidationException ex) {
            String errors = ex.getErrors().stream().map(Object::toString).collect(Collectors.joining(", "));
            fail("Shouldn't throw error but got : " + errors);
        }
    }

}