package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.config.SecurityConfiguration;
import com.skillogs.yuza.config.WebConfiguration;
import com.skillogs.yuza.domain.Course;
import com.skillogs.yuza.domain.user.Classroom;
import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.repository.ClassroomRepository;
import com.skillogs.yuza.repository.CourseRepository;
import com.skillogs.yuza.repository.UserRepository;
import com.skillogs.yuza.security.TokenAuthenticationService;
import org.assertj.core.util.Sets;
import org.bouncycastle.jcajce.provider.symmetric.TEA;
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
@WebMvcTest(ClassroomController.class)
public class ClassroomControllerTest {
    @Autowired private MockMvc mvc;

    @MockBean private TokenAuthenticationService tkpv;
    @MockBean private ClassroomRepository classroomRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private CourseRepository courseRepository;

    @Before
    public void setup() {
        when(tkpv.getAuthentication(Mockito.any()))
                .thenReturn(new TestingAuthenticationToken("aze@aze.fr", null, "ADMIN"));
    }

    @Test
    public void should_create_classroom() throws Exception {
        when(classroomRepository.create(Mockito.any(Classroom.class))).then(a -> {
            Classroom room = a.getArgumentAt(0, Classroom.class);
            room.setId("id_classroom");
            return room;
        });
        mvc.perform(post(ClassroomController.URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\":\"RILA 16\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",   is("RILA 16")))
                .andExpect(jsonPath("$.id",     is("id_classroom")));
    }

    @Test
    public void should_delete_classroom() throws Exception {
        when(classroomRepository.findOne("id_classroom")).thenReturn(new Classroom("id_classroom"));
        mvc.perform(delete(ClassroomController.URI + "/{id}", "id_classroom"))
                .andExpect(status().isOk());
        Classroom classroom = new Classroom("id_classroom");
        verify(classroomRepository).delete(classroom);
    }

    @Test
    public void failed_to_delete_with_404() throws Exception {
        when(classroomRepository.findOne("unknowed_id")).thenReturn(null);

        mvc.perform(delete(ClassroomController.URI + "/{id}", "unknowed_id"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_add_teacher_to_classroom() throws Exception {
        when(classroomRepository.findOne("id_classroom")).thenReturn(new Classroom("id_classroom"));
        when(userRepository.findOneTeacher("id_teacher")).thenReturn(new Teacher("id_teacher"));

        mvc.perform(put(ClassroomController.URI + "/{id}/teachers/{idTeacher}", "id_classroom", "id_teacher"))
                .andExpect(status().isOk());

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository).save(captor.capture());

        Classroom saved = captor.getValue();
        assertThat(saved.getId(), is("id_classroom"));
        assertThat(saved.getTeachers(), hasItem(new Teacher("id_teacher")));
    }

    @Test
    public void should_get_classrooom() throws Exception {
        Classroom classroom = new Classroom("id_classroom");
        classroom.setStudents(Sets.newLinkedHashSet(new Student("id_student_1"), new Student("id_student_2")));
        classroom.setCourses(Sets.newLinkedHashSet(new Course("id_course_1"), new Course("id_course_2"), new Course("id_course_3")));
        classroom.setTeachers(Sets.newLinkedHashSet(new Teacher("id_teacher_1")));
        when(classroomRepository.findOne("id_classroom")).thenReturn(classroom);

        mvc.perform(get(ClassroomController.URI + "/{id}", "id_classroom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("id_classroom")))
                .andExpect(jsonPath("$.courses..id", containsInAnyOrder("id_course_1", "id_course_2", "id_course_3")))
                .andExpect(jsonPath("$.students..id", containsInAnyOrder("id_student_1", "id_student_2")))
                .andExpect(jsonPath("$.teachers..id", containsInAnyOrder("id_teacher_1")));
    }

    @Test
    public void should_add_student_to_classroom() throws Exception {
        when(classroomRepository.findOne("id_classroom")).thenReturn(new Classroom("id_classroom"));
        when(userRepository.findOneStudent("id_student")).thenReturn(new Student("id_student"));

        mvc.perform(put(ClassroomController.URI + "/{id}/students/{idStudent}", "id_classroom", "id_student"))
                .andExpect(status().isOk());

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository).save(captor.capture());

        Classroom saved = captor.getValue();
        assertThat(saved.getId(), is("id_classroom"));
        assertThat(saved.getStudents(), hasItem(new Student("id_student")));
    }

    @Test
    public void should_add_course_to_classroom() throws Exception {
        when(classroomRepository.findOne("id_classroom")).thenReturn(new Classroom("id_classroom"));
        when(courseRepository.findOne("id_course")).thenReturn(new Course("id_course"));

        mvc.perform(put(ClassroomController.URI + "/{id}/courses/{idCourse}", "id_classroom", "id_course"))
                .andExpect(status().isOk());

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository).save(captor.capture());

        Classroom saved = captor.getValue();
        assertThat(saved.getId(),       is("id_classroom"));
        assertThat(saved.getCourses(),  hasItem(new Course("id_course")));
    }

    @Test
    public void should_add_course_to_classroom_with_unknowed_course() throws Exception {
        when(classroomRepository.findOne("id_classroom")).thenReturn(new Classroom("id_classroom"));
        when(courseRepository.findOne("id_course")).thenReturn(null);
        when(courseRepository.create(new Course("id_course"))).thenReturn(new Course("id_course"));

        mvc.perform(put(ClassroomController.URI + "/{id}/courses/{idCourse}", "id_classroom", "id_course"))
                .andExpect(status().isOk());

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository).save(captor.capture());

        Classroom saved = captor.getValue();
        assertThat(saved.getId(),       is("id_classroom"));
        assertThat(saved.getCourses(),  hasItem(new Course("id_course")));
    }

    @Test
    public void should_delete_course_from_classroom() throws Exception {
        Classroom classroom = new Classroom("id_classroom");
        classroom.setCourses(Sets.newLinkedHashSet(new Course("id_course_1"), new Course("id_course_2")));
        when(classroomRepository.findOne("id_classroom")).thenReturn(classroom);

        mvc.perform(delete(ClassroomController.URI + "/{id}/courses/{idCourse}", "id_classroom", "id_course_1"))
                .andExpect(status().isOk());

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository).save(captor.capture());
        Classroom saved = captor.getValue();
        assertThat(saved.getId(),       is("id_classroom"));
        assertThat(saved.getCourses(),  hasSize(1));
        assertThat(saved.getCourses(),  hasItem(new Course("id_course_2")));
    }

    @Test
    public void should_delete_teacher_from_classroom() throws Exception {
        Classroom classroom = new Classroom("id_classroom");
        classroom.setTeachers(Sets.newLinkedHashSet(new Teacher("id_teacher_1"), new Teacher("id_teacher_2")));
        when(classroomRepository.findOne("id_classroom")).thenReturn(classroom);

        mvc.perform(delete(ClassroomController.URI + "/{id}/teachers/{idTeacher}", "id_classroom", "id_teacher_1"))
                .andExpect(status().isOk());

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository).save(captor.capture());
        Classroom saved = captor.getValue();
        assertThat(saved.getId(),       is("id_classroom"));
        assertThat(saved.getTeachers(), hasSize(1));
        assertThat(saved.getTeachers(), hasItem(new Teacher("id_teacher_2")));
    }

    @Test
    public void should_delete_student_from_classroom() throws Exception {
        Classroom classroom = new Classroom("id_classroom");
        classroom.setStudents(Sets.newLinkedHashSet(new Student("id_student_1"), new Student("id_student_2")));
        when(classroomRepository.findOne("id_classroom")).thenReturn(classroom);

        mvc.perform(delete(ClassroomController.URI + "/{id}/students/{idStudent}", "id_classroom", "id_student_1"))
                .andExpect(status().isOk());

        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository).save(captor.capture());
        Classroom saved = captor.getValue();
        assertThat(saved.getId(),       is("id_classroom"));
        assertThat(saved.getStudents(), hasSize(1));
        assertThat(saved.getStudents(), hasItem(new Student("id_student_2")));
    }

    @Test
    public void should_return_all_courses_for_classroom() throws Exception {
        Classroom classroom = new Classroom("id_classroom");
        classroom.setCourses(Sets.newLinkedHashSet(new Course("id_course_1"), new Course("id_course_2")));
        when(classroomRepository.findOne("id_classroom")).thenReturn(classroom);

        mvc.perform(get(ClassroomController.URI + "/{id}/courses", "id_classroom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", containsInAnyOrder("id_course_1", "id_course_2")));
    }

    @Test
    public void should_return_all_teachers_for_classroom() throws Exception {
        Classroom classroom = new Classroom("id_classroom");
        classroom.setTeachers(Sets.newLinkedHashSet(new Teacher("id_teacher_1"), new Teacher("id_teacher_2")));
        when(classroomRepository.findOne("id_classroom")).thenReturn(classroom);

        mvc.perform(get(ClassroomController.URI + "/{id}/teachers", "id_classroom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", containsInAnyOrder("id_teacher_1", "id_teacher_2")));
    }

    @Test
    public void should_return_all_students_for_classroom() throws Exception {
        Classroom classroom = new Classroom("id_classroom");
        classroom.setStudents(Sets.newLinkedHashSet(new Student("id_student_1"), new Student("id_student_2")));
        when(classroomRepository.findOne("id_classroom")).thenReturn(classroom);

        mvc.perform(get(ClassroomController.URI + "/{id}/students", "id_classroom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", containsInAnyOrder("id_student_1", "id_student_2")));
    }

}
