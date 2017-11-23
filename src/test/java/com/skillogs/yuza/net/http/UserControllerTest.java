package com.skillogs.yuza.net.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.skillogs.yuza.config.SecurityConfiguration;
import com.skillogs.yuza.config.WebConfiguration;
import com.skillogs.yuza.domain.Role;
import com.skillogs.yuza.domain.User;
import com.skillogs.yuza.net.dto.UserDto;
import com.skillogs.yuza.net.dto.UserMapper;
import com.skillogs.yuza.net.dto.UserMapperImpl;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({UserMapperImpl.class, WebConfiguration.class, SecurityConfiguration.class})
@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private UserMapper userMapper;

    @MockBean private UserRepository userRepository;
    @MockBean private TokenAuthenticationService tkpv;

    private User createUser() {
        User john = new User("john.doe@exemple.com");
        john.setId("id");
        john.setFirstName("John");
        john.setPassword("password");
        john.setLastName("Doe");
        john.addRole(Role.STUDENT);
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
                new TestCase(Role.STUDENT, delete(UserController.URI+"/some_user_id")),
                new TestCase(Role.STUDENT, get(UserController.URI+"/some_user_id")),
                new TestCase(Role.STUDENT, put(UserController.URI+"/some_user_id").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(createUser()))),
                new TestCase(Role.STUDENT, post(UserController.URI ).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(createUser())))
        ).forEach(TestCase::checkIsSecured);
    }

    class TestCase {
        MockHttpServletRequestBuilder request;
        Role role;

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
        when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Lists.newArrayList(createUser())));

        mvc.perform(get(UserController.URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content.[0].id").value("id"));
    }

    @Test
    public void get_by_id_success() throws Exception {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(user);

        mvc.perform(get(UserController.URI+"/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void should_get_me() throws Exception {
        User user = createUser();
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken(user, null, "USER", "ADMIN", "OTHER"));
        when(userRepository.findById(user.getId()))
                .thenReturn(user);

        mvc.perform(get(UserController.URI+"/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void get_by_id_fail_404_not_found() throws Exception {
        when(userRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(get(UserController.URI+"/{id}", "unknown_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_create_user() throws Exception {
        User user = createUser();

        when(userRepository.countByEmail(user.getEmail())).thenReturn(0L);
        when(userRepository.save(user)).thenAnswer(a -> {
            User userToSave = a.getArgumentAt(0, User.class);
            if (StringUtils.isEmpty(userToSave.getPassword())) {
                fail("Password is needed when creating user");
            }
            userToSave.setId("new_id");
            return userToSave;
        });

        mvc.perform(
                post(UserController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userMapper.toDTOWithPassword(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.id",         is("new_id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.roles",      hasItem(Role.STUDENT.name())))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }


    @Test
    public void failed_to_create_user_with_validation() throws Exception {
        UserDto emptyUser = new UserDto();
        emptyUser.setEmail("toto");

        mvc.perform(
                post(UserController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$",            hasSize(5)))
                .andExpect(jsonPath("$..field",     containsInAnyOrder("email", "firstName", "lastName", "password", "roles")))
                .andExpect(jsonPath("$..message",   containsInAnyOrder("Email", "NotEmpty", "NotEmpty", "NotEmpty", "NotEmpty")));

        verifyZeroInteractions(userRepository);
    }

    @Test
    public void failed_to_create_user_with_unkown_roles() throws Exception {
        UserDto user = new UserDto();
        user.addRole("USER");
        user.addRole("MAGICIEN");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@exemple.com");

        mvc.perform(
                post(UserController.URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
        verifyZeroInteractions(userRepository);
    }


    @Test
    public void should_get_all_success_with_admin_role() throws Exception {
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN"));

        when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Lists.newArrayList(createUser())));

        mvc.perform(get(UserController.URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content.[0].id").value("id"));
    }
    // =========================================== Create New User ========================================



    @Test
    public void should_create_instructor() throws Exception {
        when(tkpv.getAuthentication(Mockito.any())).thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN"));

        User user = createUser();
        user.addRole(Role.INSTRUCTOR);

        when(userRepository.countByEmail(user.getEmail())).thenReturn(0L);
        when(userRepository.save(user)).thenAnswer(a -> {
            User u = a.getArgumentAt(0, User.class);
            u.setId("new_id");
            return u;
        });

        mvc.perform(
                post(UserController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("new_id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void test_create_user_fail_409_conflict() throws Exception {
        User user = createUser();

        when(userRepository.countByEmail(user.getEmail())).thenReturn(1L);

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_update_user() throws Exception {
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                put(UserController.URI + "/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",         is("id")))
                .andExpect(jsonPath("$.firstName",  is("John")))
                .andExpect(jsonPath("$.lastName",   is("Doe")))
                .andExpect(jsonPath("$.email",      is("john.doe@exemple.com")));
    }

    @Test
    public void fail_update_user_with_404_not_found() throws Exception {
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(null);

        mvc.perform(
                put(UserController.URI + "/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    // =========================================== Delete User ============================================
    @Test
    public void should_delete_user() throws Exception {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(user);
        doNothing().when(userRepository).delete(user);

        mvc.perform(
                delete(UserController.URI+"/{id}", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void test_delete_user_fail_404_not_found() throws Exception {
        when(userRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(
                delete(UserController.URI+"/unknown_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_authenticate_user() throws Exception {

        User john = createUser();
        UserController.UserCredentials user = new UserController.UserCredentials();
        user.setEmail(john.getEmail());
        user.setPassword(john.getPassword());

        when(userRepository.findByEmailAndPassword(
                user.getEmail(),
                user.getPassword()))
                .thenReturn(john);

        mvc.perform(post(UserController.URI + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void fail_authenticate_user_with_404_not_found() throws Exception {

        UserController.UserCredentials user = new UserController.UserCredentials();
        user.setEmail("unknown@user.com");
        user.setPassword("password");

        when(userRepository.findByEmailAndPassword(
                user.getEmail(),
                user.getPassword()))
                .thenReturn(null);

        mvc.perform(post(UserController.URI + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }
    // =========================================== Courses User ===================================
    @Test
    public void should_get_courses_success() throws Exception {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(user);

        mvc.perform(get(UserController.URI+"/{id}/courses", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void failed_to_get_courses_with_404_not_found() throws Exception {
        when(userRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(get(UserController.URI+"/unknown_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void add_courses_success() throws Exception {
        User user = createUser();
        user.addCourse("toto");

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                put(UserController.URI + "/{id}/courses/toto", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$").isArray());

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);

    }
    @Test
    public void add_courses_fail_404_not_found() throws Exception {
        when(userRepository.findById("gnii")).thenReturn(null);

        mvc.perform(put(UserController.URI+"/{id}/courses/{course}", "gnii","bla"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById("gnii");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void delete_courses_success() throws Exception {
        User user = createUser();
        user.addCourse("toto");

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                delete(UserController.URI + "/{id}/courses/toto", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);

    }

    @Test
    public void delete_courses_fail_404_not_found() throws Exception {
        User user = createUser();
        user.addCourse("toto");

        when(userRepository.findById(user.getId())).thenReturn(user);

        mvc.perform(
                delete(UserController.URI + "/{id}/courses/gniiiii", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void delete_all_courses_success() throws Exception {
        User user = createUser();
        user.addCourse("toto");
        user.addCourse("tutu");

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                delete(UserController.URI + "/{id}/courses", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void delete_all_courses_fail_404_not_found() throws Exception {

        when(userRepository.findById("gnii")).thenReturn(null);

        mvc.perform(delete(UserController.URI+"/{id}/courses", "gnii"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById("gnii");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void shouldMapUserToDto() {
        User user = new User("john.doe@exemple.com");
        user.setFirstName("bob");

        UserDto userDto = userMapper.toDTO(user);

        assertThat(userDto.getFirstName(),  is("bob"));
        assertThat(userDto.getEmail(),      is("john.doe@exemple.com"));
    }

}
