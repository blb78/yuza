package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.config.SecurityConfiguration;
import com.skillogs.yuza.config.WebConfiguration;
import com.skillogs.yuza.domain.account.Account;
import com.skillogs.yuza.domain.account.Role;
import com.skillogs.yuza.domain.user.Cursus;
import com.skillogs.yuza.domain.user.Promotion;
import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.repository.PromotionRepository;
import com.skillogs.yuza.repository.UserRepository;
import com.skillogs.yuza.security.TokenAuthenticationService;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({WebConfiguration.class, SecurityConfiguration.class})
@EnableSpringDataWebSupport
@RunWith(SpringRunner.class)
@WebMvcTest(PromotionController.class)
public class PromotionControllerTest {
    @Autowired private MockMvc mvc;

    @MockBean private TokenAuthenticationService tkpv;
    @MockBean private PromotionRepository promotionRepository;
    @MockBean private UserRepository userRepository;

    @Before
    public void setup() {
        Account account = new Account("test@email.com");
        account.setRole(Role.ADMIN);
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken(account, null, "ADMIN"));
    }

    @Test
    public void should_get_promotions() throws Exception {
        Account account = new Account("test@email.com");
        account.setRole(Role.ADMIN);
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken(account, null, account.getRole().name()));
        when(promotionRepository.findAll())
                .thenReturn(Sets.newLinkedHashSet(new Promotion("id_promotion")));

        mvc.perform(get(PromotionController.URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is("id_promotion")));
    }
    @Test
    public void failed_get_promotions() throws Exception {

        Account account = new Account("test@email.com");
        account.setRole(Role.STUDENT);
        account.setId("id_student_1");
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken(account, null, account.getRole().name()));

        mvc.perform(get(PromotionController.URI))
                .andExpect(status().isForbidden());
    }

    @Test
    public void should_get_teacher_promotions() throws Exception {
        Promotion promotion = new Promotion("id_promotion");
        promotion.setTeachers(Sets.newLinkedHashSet(new Teacher("id_teacher_1")));
        when(promotionRepository.findAll("id_teacher_1"))
                .thenReturn(Sets.newLinkedHashSet(promotion));
        Account account = new Account("test@email.com");
        account.setRole(Role.TEACHER);
        account.setId("id_teacher_1");
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken(account, null, account.getRole().name()));

        mvc.perform(get(PromotionController.URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is("id_promotion")));
    }


    @Test
    public void should_create_promotion() throws Exception {
        when(promotionRepository.create(Mockito.any(Promotion.class))).then(a -> {
            Promotion room = a.getArgumentAt(0, Promotion.class);
            room.setId("id_promotion");
            return room;
        });
        mvc.perform(post(PromotionController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\":\"RILA 16\", \"cursus\": { \"id\":\"id_cursus\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",       is("RILA 16")))
                .andExpect(jsonPath("$.cursus.id",  is("id_cursus")))
                .andExpect(jsonPath("$.id",         is("id_promotion")));
    }

    @Test
    public void should_delete_promotion() throws Exception {
        when(promotionRepository.findOne("id_promotion")).thenReturn(new Promotion("id_promotion"));
        mvc.perform(delete(PromotionController.URI + "/{id}", "id_promotion"))
                .andExpect(status().isOk());
        Promotion promotion = new Promotion("id_promotion");
        verify(promotionRepository).delete(promotion);
    }

    @Test
    public void failed_to_delete_with_404() throws Exception {
        when(promotionRepository.findOne("unknowed_id")).thenReturn(null);

        mvc.perform(delete(PromotionController.URI + "/{id}", "unknowed_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_add_teacher_to_promotion() throws Exception {
        when(promotionRepository.findOne("id_promotion")).thenReturn(new Promotion("id_promotion"));
        when(userRepository.findOneTeacher("id_teacher")).thenReturn(new Teacher("id_teacher"));

        mvc.perform(put(PromotionController.URI + "/{id}/teachers/{idTeacher}", "id_promotion", "id_teacher"))
                .andExpect(status().isOk());

        ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
        verify(promotionRepository).save(captor.capture());

        Promotion saved = captor.getValue();
        assertThat(saved.getId(), is("id_promotion"));
        assertThat(saved.getTeachers(), hasItem(new Teacher("id_teacher")));
    }

    @Test
    public void should_get_promotion() throws Exception {
        Promotion promotion = new Promotion("id_promotion");
        promotion.setStudents(Sets.newLinkedHashSet(new Student("id_student_1"), new Student("id_student_2")));
        promotion.setCursus(new Cursus("id_cursus"));
        promotion.setTeachers(Sets.newLinkedHashSet(new Teacher("id_teacher_1")));
        when(promotionRepository.findOne("id_promotion")).thenReturn(promotion);

        mvc.perform(get(PromotionController.URI + "/{id}", "id_promotion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("id_promotion")))
                .andExpect(jsonPath("$.cursus.id",    is("id_cursus")))
                .andExpect(jsonPath("$.students..id", containsInAnyOrder("id_student_1", "id_student_2")))
                .andExpect(jsonPath("$.teachers..id", containsInAnyOrder("id_teacher_1")));
    }

    @Test
    public void should_add_student_to_promotion() throws Exception {
        when(promotionRepository.findOne("id_promotion")).thenReturn(new Promotion("id_promotion"));
        when(userRepository.findOneStudent("id_student")).thenReturn(new Student("id_student"));

        mvc.perform(put(PromotionController.URI + "/{id}/students/{idStudent}", "id_promotion", "id_student"))
                .andExpect(status().isOk());

        ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
        verify(promotionRepository).save(captor.capture());

        Promotion saved = captor.getValue();
        assertThat(saved.getId(), is("id_promotion"));
        assertThat(saved.getStudents(), hasItem(new Student("id_student")));
    }


    @Test
    public void should_delete_teacher_from_promotion() throws Exception {
        Promotion promotion = new Promotion("id_promotion");
        promotion.setTeachers(Sets.newLinkedHashSet(new Teacher("id_teacher_1"), new Teacher("id_teacher_2")));
        when(promotionRepository.findOne("id_promotion")).thenReturn(promotion);

        mvc.perform(delete(PromotionController.URI + "/{id}/teachers/{idTeacher}", "id_promotion", "id_teacher_1"))
                .andExpect(status().isOk());

        ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
        verify(promotionRepository).save(captor.capture());
        Promotion saved = captor.getValue();
        assertThat(saved.getId(),       is("id_promotion"));
        assertThat(saved.getTeachers(), hasSize(1));
        assertThat(saved.getTeachers(), hasItem(new Teacher("id_teacher_2")));
    }

    @Test
    public void should_delete_student_from_promotion() throws Exception {
        Promotion promotion = new Promotion("id_promotion");
        promotion.setStudents(Sets.newLinkedHashSet(new Student("id_student_1"), new Student("id_student_2")));
        when(promotionRepository.findOne("id_promotion")).thenReturn(promotion);

        mvc.perform(delete(PromotionController.URI + "/{id}/students/{idStudent}", "id_promotion", "id_student_1"))
                .andExpect(status().isOk());

        ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
        verify(promotionRepository).save(captor.capture());
        Promotion saved = captor.getValue();
        assertThat(saved.getId(),       is("id_promotion"));
        assertThat(saved.getStudents(), hasSize(1));
        assertThat(saved.getStudents(), hasItem(new Student("id_student_2")));
    }

    @Test
    public void should_return_all_teachers_for_promotion() throws Exception {
        Promotion promotion = new Promotion("id_promotion");
        promotion.setTeachers(Sets.newLinkedHashSet(new Teacher("id_teacher_1"), new Teacher("id_teacher_2")));
        when(promotionRepository.findOne("id_promotion")).thenReturn(promotion);

        mvc.perform(get(PromotionController.URI + "/{id}/teachers", "id_promotion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", containsInAnyOrder("id_teacher_1", "id_teacher_2")));
    }

    @Test
    public void should_return_all_students_for_promotion() throws Exception {
        Promotion promotion = new Promotion("id_promotion");
        promotion.setStudents(Sets.newLinkedHashSet(new Student("id_student_1"), new Student("id_student_2")));
        when(promotionRepository.findOne("id_promotion")).thenReturn(promotion);

        mvc.perform(get(PromotionController.URI + "/{id}/students", "id_promotion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", containsInAnyOrder("id_student_1", "id_student_2")));
    }

}
