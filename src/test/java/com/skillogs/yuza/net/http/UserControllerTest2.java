package com.skillogs.yuza.net.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillogs.yuza.config.SecurityConfiguration;
import com.skillogs.yuza.config.WebConfiguration;
import com.skillogs.yuza.domain.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({UserMapperImpl.class, WebConfiguration.class, SecurityConfiguration.class})
@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest2 {

    @Autowired
    private MockMvc mvc;
    @Autowired private ObjectMapper mapper;


    @MockBean
    private UserRepository userRepository;
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

    }



    // =========================================== Get All Users ==========================================

    @Test
    public void get_all_success() throws Exception {
        User john1 = createUser();
        List<User> list = new ArrayList<>();
        list.add(john1);
        Page<User> page = new PageImpl<>(list);
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
    // =========================================== Create New User ========================================

    @Test
    public void should_create_instructor() throws Exception {


        User user = new User();

        user.setFirstName("John");
        user.setPassword("password");
        user.setLastName("Doe");
        user.setEmail("doe.doe@exemple.com");
        user.addRole("INSTRUCTOR");

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
    public void failed_to_create_instructor() throws Exception {

        TestingAuthenticationToken auth = new TestingAuthenticationToken("aze@aze.fr", null, "USER");
        when(tkpv.getAuthentication(Mockito.any())).thenReturn(auth);


        User user = new User();

        user.setFirstName("John");
        user.setPassword("password");
        user.setLastName("Doe");
        user.setEmail("doe.doe@exemple.com");
        user.addRole("INSTRUCTOR");



        mvc.perform(
                post(UserController.URI )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());

        verifyNoMoreInteractions(userRepository);
    }


}
