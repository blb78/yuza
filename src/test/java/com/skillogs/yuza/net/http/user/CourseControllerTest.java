package com.skillogs.yuza.net.http.user;

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
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({WebConfiguration.class, SecurityConfiguration.class})
@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(CourseController.class)
public class CourseControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private UserRepository userRepository;
    @MockBean private TokenAuthenticationService tkpv;

    @Before
    public void setup() {
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN"));
    }

    @Test
    public void should_get_courses() throws Exception {
        User user = build(User::new,
                u -> u.setId("id"),
                u -> u.follow("course1"),
                u -> u.follow("course2"));
        when(userRepository.findById(user.getId())).thenReturn(user);

        mvc.perform(get(CourseController.URI, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",    hasSize(user.getCourses().size())))
                .andExpect(jsonPath("$",  containsInAnyOrder("course1", "course2")));
    }

    @Test
    public void failed_to_get_courses_with_404_not_found() throws Exception {
        when(userRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(get(CourseController.URI, "unknown_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_add_courses() throws Exception {
        User user = build(User::new,
                u -> u.setId("id"),
                u -> u.follow("course1"));

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                put(CourseController.URI + "/{courseId}", user.getId(), "course2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", containsInAnyOrder("course1", "course2")));
    }

    @Test
    public void failed_to_add_courses_with_404() throws Exception {
        when(userRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(put(CourseController.URI+"/{course}", "unknown_id", "unknown_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_delete_courses() throws Exception {
        User user = build(User::new,
                u -> u.setId("id"),
                u -> u.follow("course1"));

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                delete(CourseController.URI + "/{courseId}", user.getId(), "course1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void failed_to_delete_courses_with_404() throws Exception {
        User user = build(User::new,
                u -> u.setId("id"),
                u -> u.follow("course1"));

        when(userRepository.findById(user.getId())).thenReturn(user);

        mvc.perform(
                delete(CourseController.URI + "/{courseId)", user.getId(), "unknown_id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_delete_all_courses() throws Exception {
        User user = build(User::new,
                u -> u.setId("id"),
                u -> u.follow("course1"),
                u -> u.follow("course2"));

        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        mvc.perform(
                delete(CourseController.URI, user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void failed_to_delete_all_courses_with_404() throws Exception {

        when(userRepository.findById("unknown_id")).thenReturn(null);

        mvc.perform(delete(CourseController.URI, "unknown_id"))
                .andExpect(status().isNotFound());
    }

    @SafeVarargs
    private final <T> T build(Supplier<T> factory, Consumer<T>... cons) {
        T instance = factory.get();
        for (Consumer<T> con : cons) {
            con.accept(instance);
        }
        return instance;
    }
}