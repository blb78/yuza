package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.domain.Course;
import com.skillogs.yuza.domain.account.Account;
import com.skillogs.yuza.domain.user.Classroom;
import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.repository.ClassroomRepository;
import com.skillogs.yuza.repository.CourseRepository;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@RestController
@RequestMapping(ClassroomController.URI)
public class ClassroomController {
    public static final String URI = "/classrooms";

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public ClassroomController(ClassroomRepository classroomRepository,
                               UserRepository userRepository,
                               CourseRepository courseRepository) {
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    public ResponseEntity<Set<Classroom>> findAll(@AuthenticationPrincipal Account authenticated){
        switch (authenticated.getRole()){
            case ADMIN:
                return ResponseEntity.ok(classroomRepository.findAll());
            case TEACHER:
                return ResponseEntity.ok(classroomRepository.findAll(authenticated.getId()));
            default:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public Classroom create(@RequestBody Classroom classroom) {
        return classroomRepository.create(classroom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        if (classroomRepository.findOne(id) == null) {
            return ResponseEntity.notFound().build();
        }
        classroomRepository.delete(new Classroom(id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getOne(@PathVariable String id) {
        return Optional.ofNullable(classroomRepository.findOne(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/teachers/{idTeacher}")
    public ResponseEntity addTeacher(@PathVariable String id, @PathVariable String idTeacher) {
        Classroom classroom = classroomRepository.findOne(id);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }

        Teacher teacher = userRepository.findOneTeacher(idTeacher);
        if (teacher == null) {
            return ResponseEntity.notFound().build();
        }

        classroom.add(teacher);
        classroomRepository.save(classroom);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/students/{idStudent}")
    public ResponseEntity addStudent(@PathVariable String id, @PathVariable String idStudent) {
        Classroom classroom = classroomRepository.findOne(id);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }

        Student student = userRepository.findOneStudent(idStudent);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        classroom.add(student);
        classroomRepository.save(classroom);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/courses/{idCourse}")
    public ResponseEntity addCourse(@PathVariable String id, @PathVariable String idCourse) {
        Classroom classroom = classroomRepository.findOne(id);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }

        Course course = courseRepository.findOne(idCourse);
        if (course == null) {
            course = courseRepository.create(new Course(idCourse));
        }

        classroom.add(course);
        classroomRepository.save(classroom);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/courses/{idCourse}")
    public ResponseEntity deleteCourse(@PathVariable String id, @PathVariable String idCourse) {
        return deleteFromClassroom(id, classroom -> classroom.remove(new Course(idCourse)));
    }

    @DeleteMapping("/{id}/teachers/{idTeacher}")
    public ResponseEntity deleteTeacher(@PathVariable String id, @PathVariable String idTeacher) {
        return deleteFromClassroom(id, classroom -> classroom.remove(new Teacher(idTeacher)));
    }

    @DeleteMapping("/{id}/students/{idStudent}")
    public ResponseEntity deleteStudent(@PathVariable String id, @PathVariable String idStudent) {
        return deleteFromClassroom(id, classroom -> classroom.remove(new Student(idStudent)));
    }

    private ResponseEntity deleteFromClassroom(String id, Consumer<Classroom> cons) {
        Classroom classroom = classroomRepository.findOne(id);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }
        cons.accept(classroom);
        classroomRepository.save(classroom);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<Set<Course>> getCourses(@PathVariable String id) {
        return retrieveFromClassroom(id, Classroom::getCourses);
    }

    @GetMapping("/{id}/teachers")
    public ResponseEntity<Set<Teacher>> getTeachers(@PathVariable String id) {
        return retrieveFromClassroom(id, Classroom::getTeachers);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<Set<Student>> getStudents(@PathVariable String id) {
        return retrieveFromClassroom(id, Classroom::getStudents);
    }

    private <T> ResponseEntity<T> retrieveFromClassroom(@PathVariable String id, Function<Classroom, T> mapper) {
        return Optional.ofNullable(classroomRepository.findOne(id))
                .map(mapper)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
