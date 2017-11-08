package com.skillogs.yuza.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillogs.yuza.domain.User;

import com.skillogs.yuza.net.http.UserDto;
import com.skillogs.yuza.repository.UserRepository;
import com.skillogs.yuza.security.StatelessAuthenticationFilter;
import com.skillogs.yuza.security.TokenAuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.MatcherAssert.assertThat;



@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class, secure = false)
public class UserControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private UserRepository userRepository;
    @MockBean private TokenAuthenticationService authenticationService;


    @Before
    public void setup() {
        when(authenticationService.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken("aze@æze.fr", null));
    }

//  url : /users/unknow_id
    @Test
    public void should_return_404() throws Exception {
        mvc.perform(get(UserController.URI+ "/unkown_id"))
                .andExpect(status().isNotFound());
    }

//  url : /users/authenticate
    @Test
    public void should_return_200() throws Exception {
        User john = new User();
        john.setId("id");
        john.setFirstName("John");
        john.setPassword("password");
        john.setLastName("Doe");
        john.setEmail("john.doe@exemple.com");

        UserController.UserCredentials user = new UserController.UserCredentials();
        user.setEmail("john.doe@exemple.com");
        user.setPassword("password");


        when(userRepository.findByEmailAndPassword(
                user.getEmail(),
                user.getPassword()))
                .thenReturn(john);

        mvc.perform(post(UserController.URI + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

    }

//    url : /users
    @Test
    public void should_return_a_user() throws Exception {
        User john = new User();
        john.setId("id");
        john.setFirstName("John");
        john.setPassword("password");
        john.setLastName("Doe");
        john.setEmail("john.doe@exemple.com");
        when(userRepository.findById(john.getId())).thenReturn(john);

        mvc.perform(get(UserController.URI + "/id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("id")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email", is("john.doe@exemple.com")));

    }
    @Test
    public void should_return_an_updated_user() throws Exception {
        User john = new User();
        john.setId("id");
        john.setFirstName("John");
        john.setPassword("password");
        john.setLastName("Doe");
        john.setEmail("john.doe@exemple.com");

        when(userRepository.findById(john.getId())).thenReturn(john);
        when(userRepository.save(john)).thenReturn(john);

        mvc.perform(put(UserController.URI + "/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(john)))
                .andExpect(jsonPath("$.id", is("id")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email", is("john.doe@exemple.com")));



    }
    @Test
    public void should_return_a_new_user() throws Exception {
        User newUser = new User();

        newUser.setFirstName("John");
        newUser.setPassword("password");
        newUser.setLastName("Doe");
        newUser.setEmail("doe.doe@exemple.com");

        when(userRepository.save(newUser)).thenAnswer(a -> {
            User userToSave = a.getArgumentAt(0, User.class);
            userToSave.setId("new_id");
            return userToSave;
        });
        mvc.perform(post(UserController.URI )
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("new_id")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email", is("doe.doe@exemple.com")));
    }


    @Test
    public void shouldMapUserToDto() {
        //given
        User user = new User();
        user.setFirstName("bob");
        //when
        UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);
        //then
        assertThat(userDto.getFirstName(), is("bob"));
    }



}
