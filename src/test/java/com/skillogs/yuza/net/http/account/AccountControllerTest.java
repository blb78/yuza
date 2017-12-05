package com.skillogs.yuza.net.http.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.skillogs.yuza.config.SecurityConfiguration;
import com.skillogs.yuza.config.WebConfiguration;
import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.domain.account.Account;
import com.skillogs.yuza.domain.account.Role;
import com.skillogs.yuza.net.dto.AccountDto;
import com.skillogs.yuza.net.dto.AccountMapper;
import com.skillogs.yuza.net.dto.AccountMapperImpl;
import com.skillogs.yuza.net.exception.ValidationException;
import com.skillogs.yuza.net.exception.ValidatorError;
import com.skillogs.yuza.net.validator.impl.AccountValidator;
import com.skillogs.yuza.repository.AccountRepository;
import com.skillogs.yuza.repository.UserRepository;
import com.skillogs.yuza.security.TokenAuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({AccountMapperImpl.class, WebConfiguration.class, SecurityConfiguration.class})
@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private AccountMapper accountMapper;

    @MockBean private AccountRepository accountRepository;
    @MockBean private TokenAuthenticationService tkpv;
    @MockBean private AccountValidator validator;
    @MockBean private UserRepository userRepository;

    @SafeVarargs
    private final Account createAccount(Consumer<Account>... cons) {
        Account john = new Account("john.doe@exemple.com");
        john.setId("id");
        john.setFirstName("John");
        john.setPassword("password");
        john.setLastName("Doe");
        john.setRole(Role.STUDENT);
        for (Consumer<Account> con : cons) {
            con.accept(john);
        }
        return john;
    }


    @Before
    public void setup() {
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN"));
    }

    @Test
    public void should_endpoint_de_secured() throws JsonProcessingException {
        Arrays.asList(
                new TestCase(Role.STUDENT, delete(AccountController.URI+"/some_user_id")),
                new TestCase(Role.STUDENT, get(AccountController.URI+"/some_user_id")),
                new TestCase(Role.STUDENT, put(AccountController.URI+"/some_user_id").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(createAccount()))),
                new TestCase(Role.STUDENT, post(AccountController.URI ).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(createAccount())))
        ).forEach(TestCase::checkIsSecured);
    }

    class TestCase {
        final MockHttpServletRequestBuilder request;
        final Role role;

        TestCase(Role role, MockHttpServletRequestBuilder request) {
            this.request = request;
            this.role = role;
        }

        void checkIsSecured(){
            try {
                when(tkpv.getAuthentication(Mockito.any())).thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, role.name()));
                mvc.perform(request).andExpect(status().isForbidden());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void should_get_all_success() throws Exception {
        when(accountRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Lists.newArrayList(createAccount())));

        mvc.perform(get(AccountController.URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content.[0].id").value("id"));
    }

    @Test
    public void should_get_user_by_id() throws Exception {
        Account account = createAccount();
        when(accountRepository.findById(account.getId())).thenReturn(account);

        mvc.perform(get(AccountController.URI+"/{id}", account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void should_get_me() throws Exception {
        Account account = createAccount();
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken(account, null, "USER", "ADMIN", "OTHER"));
        when(accountRepository.findById(account.getId()))
                .thenReturn(account);

        mvc.perform(get(AccountController.URI+"/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void failed_to_get_user_by_id_with_404() throws Exception {
        when(accountRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(get(AccountController.URI+"/{id}", "unknown_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_create_account_with_role_student() throws Exception {
        Account account = createAccount(
                a -> a.setId(null),
                a -> a.setRole(Role.STUDENT));

        when(accountRepository.countByEmail(account.getEmail())).thenReturn(0L);
        when(accountRepository.save(account)).thenAnswer(a -> {
            Account accountToSave = a.getArgumentAt(0, Account.class);
            if (StringUtils.isEmpty(accountToSave.getPassword())) {
                fail("Password is needed when creating account");
            }
            account.setId("new_id");
            accountToSave.setId("new_id");
            return accountToSave;
        });

        mvc.perform(
                post(AccountController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountMapper.toDTOWithPassword(account))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id",         is("new_id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.role",       is(Role.STUDENT.name())))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));

        Student expected = new Student(account.getId());
        verify(userRepository).save(expected);
    }

    @Test
    public void should_create_account_with_role_teacher() throws Exception {
        Account account = createAccount(
                a -> a.setId(null),
                a -> a.setRole(Role.TEACHER));

        when(accountRepository.countByEmail(account.getEmail())).thenReturn(0L);
        when(accountRepository.save(account)).thenAnswer(a -> {
            Account accountToSave = a.getArgumentAt(0, Account.class);
            if (StringUtils.isEmpty(accountToSave.getPassword())) {
                fail("Password is needed when creating account");
            }
            account.setId("new_id");
            accountToSave.setId("new_id");
            return accountToSave;
        });

        mvc.perform(
                post(AccountController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountMapper.toDTOWithPassword(account))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id",         is("new_id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.role",       is(Role.TEACHER.name())))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));

        Teacher expected = new Teacher(account.getId());
        verify(userRepository).save(expected);
    }


    @Test
    public void failed_to_create_user_with_validation() throws Exception {
        AccountDto emptyUser = new AccountDto();

        ValidationException exception = new ValidationException(Arrays.asList(
                new ValidatorError("field1", "error1"),
                new ValidatorError("field2", "error2")));
        doThrow(exception).when(validator).validate(Mockito.any(AccountDto.class));

        mvc.perform(
                post(AccountController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$",            hasSize(2)))
                .andExpect(jsonPath("$..field",     containsInAnyOrder("field1", "field2")))
                .andExpect(jsonPath("$..message",   containsInAnyOrder("error1", "error2")));
    }

    @Test
    public void should_get_all_users_with_admin_role() throws Exception {
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN"));

        when(accountRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Lists.newArrayList(createAccount())));

        mvc.perform(get(AccountController.URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content.[0].id").value("id"));
    }

    @Test
    public void should_create_instructor() throws Exception {
        when(tkpv.getAuthentication(Mockito.any())).thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN"));

        Account account = createAccount();
        account.setRole(Role.TEACHER);

        when(accountRepository.countByEmail(account.getEmail())).thenReturn(0L);
        when(accountRepository.save(account)).thenAnswer(a -> {
            Account u = a.getArgumentAt(0, Account.class);
            u.setId("new_id");
            return u;
        });

        mvc.perform(
                post(AccountController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("new_id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void should_update_user() throws Exception {
        Account account = createAccount();
        account.setPassword(null);

        when(accountRepository.findById(account.getId())).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        mvc.perform(
                put(AccountController.URI + "/{id}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountMapper.toDTO(account))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void failed_to_update_user_with_404() throws Exception {
        Account account = createAccount();

        when(accountRepository.findById(account.getId())).thenReturn(null);

        mvc.perform(
                put(AccountController.URI + "/{id}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account)))
                .andExpect(status().isNotFound());
    }

    // =========================================== Delete Account ============================================
    @Test
    public void should_delete_account_user() throws Exception {
        Account account = createAccount(a -> a.setRole(Role.STUDENT));
        when(accountRepository.findById(account.getId())).thenReturn(account);

        mvc.perform(
                delete(AccountController.URI+"/{id}", account.getId()))
                .andExpect(status().isOk());
        verify(accountRepository).delete(account);
        verify(userRepository).delete(new Student(account.getId()));
    }

    @Test
    public void should_delete_account_teacher() throws Exception {
        Account account = createAccount(a -> a.setRole(Role.TEACHER));
        when(accountRepository.findById(account.getId())).thenReturn(account);

        mvc.perform(
                delete(AccountController.URI+"/{id}", account.getId()))
                .andExpect(status().isOk());
        verify(accountRepository).delete(account);
        verify(userRepository).delete(new Teacher(account.getId()));
    }

    @Test
    public void test_delete_user_fail_404_not_found() throws Exception {
        when(accountRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(
                delete(AccountController.URI+"/unknown_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_authenticate_user() throws Exception {

        Account john = createAccount();
        AccountController.AccountCredentials user = new AccountController.AccountCredentials();
        user.setEmail(john.getEmail());
        user.setPassword(john.getPassword());

        when(accountRepository.findByEmailAndPassword(
                user.getEmail(),
                user.getPassword()))
                .thenReturn(john);

        mvc.perform(post(AccountController.URI + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void fail_authenticate_user_with_404_not_found() throws Exception {

        AccountController.AccountCredentials user = new AccountController.AccountCredentials();
        user.setEmail("unknown@account.com");
        user.setPassword("password");

        when(accountRepository.findByEmailAndPassword(
                user.getEmail(),
                user.getPassword()))
                .thenReturn(null);

        mvc.perform(post(AccountController.URI + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldMapUserToDto() {
        Account account = new Account("john.doe@exemple.com");
        account.setFirstName("bob");

        AccountDto accountDto = accountMapper.toDTO(account);

        assertThat(accountDto.getFirstName(),  is("bob"));
        assertThat(accountDto.getEmail(),      is("john.doe@exemple.com"));
    }

}
