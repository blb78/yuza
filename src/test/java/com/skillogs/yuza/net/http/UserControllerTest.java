package com.skillogs.yuza.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillogs.yuza.domain.User;
import com.skillogs.yuza.repository.UserRepository;
import com.skillogs.yuza.security.TokenAuthenticationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(UserMapperImpl.class)
@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(value = {UserController.class}, secure = false)
public class UserControllerTest {


    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private UserMapperImpl userMapper;




    @MockBean private UserDetailsService detailsService;
    @MockBean private UserRepository userRepository;
    @MockBean private TokenAuthenticationService tkpv;



    private User createUser() {
        User john = new User();
        john.setId("id");
        john.setFirstName("John");
        john.setPassword("password");
        john.setLastName("Doe");
        john.setEmail("john.doe@exemple.com");
        return john;
    }


    @Before
    public void setup() {

        TestingAuthenticationToken auth = new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN");
        when(tkpv.getAuthentication(Mockito.any())).thenReturn(auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @After
    public void clean() {
        SecurityContextHolder.clearContext();
    }



    // =========================================== Get All Users ==========================================

    @Test
    public void get_all_success() throws Exception {
        User john1 = createUser();
        List<User> list = new ArrayList<User>();
        list.add(john1);
        Page<User> page = new PageImpl<User>(list);
        when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        mvc.perform(get(UserController.URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content.[0].id").value("id"));

        verify(userRepository).findAll(Mockito.any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }


    // =========================================== Get User By ID =========================================

    @Test
    public void get_by_id_success() throws Exception {
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(user);

        mvc.perform(get(UserController.URI+"/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("id")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@exemple.com")));

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void get_by_id_fail_404_not_found() throws Exception {

        when(userRepository.findById("gnii")).thenReturn(null);

        mvc.perform(get(UserController.URI+"/{id}", "gnii"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById("gnii");
        verifyNoMoreInteractions(userRepository);
    }

    // =========================================== Create New User ========================================

    @Test
    public void create_user_success() throws Exception {
        User user = new User();

        user.setFirstName("John");
        user.setPassword("password");
        user.setLastName("Doe");
        user.setEmail("doe.doe@exemple.com");

        when(userRepository.countByEmail(user.getEmail())).thenReturn((long) 0);
        when(userRepository.save(user)).thenAnswer(a -> {
            User userToSave = a.getArgumentAt(0, User.class);
            userToSave.setId("new_id");
            return userToSave;
        });

        mvc.perform(
                post(UserController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("new_id")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("doe.doe@exemple.com")));

        verify(userRepository).countByEmail(user.getEmail());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void test_create_user_fail_409_conflict() throws Exception {
        User user = createUser();

        when(userRepository.countByEmail(user.getEmail())).thenReturn((long) 1);

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isConflict());

        verify(userRepository).countByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    // =========================================== Update User ===================================
    @Test
    public void update_user_success() throws Exception {
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                put(UserController.URI + "/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("id")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@exemple.com")));

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    public void update_user_fail_404_not_found() throws Exception {
        User user = createUser();
        user.setId("toto");

        when(userRepository.findById(user.getId())).thenReturn(null);

        mvc.perform(
                put(UserController.URI + "/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    // =========================================== Delete User ============================================
    @Test
    public void delete_user_success() throws Exception {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(user);
        doNothing().when(userRepository).delete(user);
        mvc.perform(
                delete(UserController.URI+"/{id}", user.getId()))
                .andExpect(status().isOk());
        verify(userRepository).findById(user.getId());
        verify(userRepository).delete(user);
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    public void test_delete_user_fail_404_not_found() throws Exception {
        User user = new User();
        user.setId("toto");

        when(userRepository.findById(user.getId())).thenReturn(null);

        mvc.perform(
                delete(UserController.URI+"/{id}", user.getId()))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    // =========================================== Authenticate User ===================================
    @Test
    public void authentication_success() throws Exception {

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

        verify(userRepository).findByEmailAndPassword(user.getEmail(),user.getPassword());
        verifyNoMoreInteractions(userRepository);

    }
    @Test
    public void authentication_fail_404_not_found() throws Exception {

        User john = createUser();
        UserController.UserCredentials user = new UserController.UserCredentials();
        user.setEmail("gniii@gniii.com");
        user.setPassword("gniii");

        when(userRepository.findByEmailAndPassword(
                user.getEmail(),
                user.getPassword()))
                .thenReturn(null);


        mvc.perform(post(UserController.URI + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());

        verify(userRepository).findByEmailAndPassword(user.getEmail(),user.getPassword());
        verifyNoMoreInteractions(userRepository);

    }
    // =========================================== Courses User ===================================
    @Test
    public void get_courses_success() throws Exception {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(user);



        mvc.perform(get(UserController.URI+"/{id}/courses", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$").isArray());

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);

    }
    @Test
    public void get_courses_fail_404_not_found() throws Exception {

        when(userRepository.findById("gnii")).thenReturn(null);

        mvc.perform(get(UserController.URI+"/{id}", "gnii"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById("gnii");
        verifyNoMoreInteractions(userRepository);

    }
    // =========================================== Add Course User ===================================
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
    // =========================================== Delete Course User ===================================
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

    // =========================================== Delete ALL Courses User ===================================

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
        //given
        User user = new User();
        user.setFirstName("bob");
        //when

        UserDto userDto = userMapper.toDTO(user);
        //then
        assertThat(userDto.getFirstName(), is("bob"));

    }


}
