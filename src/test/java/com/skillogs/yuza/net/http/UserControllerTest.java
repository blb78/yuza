package com.skillogs.yuza.net.http;

import com.skillogs.yuza.domain.User;

import com.skillogs.yuza.net.http.UserDto;
import com.skillogs.yuza.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.MatcherAssert.assertThat;



@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private UserRepository userRepository;



    @Test
    public void should_return_401() throws Exception {
        mvc.perform(get(UserController.URI ))
                .andExpect(status().isUnauthorized());
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
