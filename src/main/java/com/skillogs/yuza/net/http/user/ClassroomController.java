package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.domain.Course;
import com.skillogs.yuza.domain.user.Classroom;
import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.repository.ClassroomRepository;
import com.skillogs.yuza.repository.CourseRepository;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Classroom classroom = classroomRepository.findOne(id);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }

        classroom.remove(new Course(idCourse));
        classroomRepository.save(classroom);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/teachers/{idTeacher}")
    public ResponseEntity deleteTeacher(@PathVariable String id, @PathVariable String idTeacher) {
        Classroom classroom = classroomRepository.findOne(id);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }

        classroom.remove(new Teacher(idTeacher));
        classroomRepository.save(classroom);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/students/{idStudent}")
    public ResponseEntity deleteStudent(@PathVariable String id, @PathVariable String idStudent) {
        Classroom classroom = classroomRepository.findOne(id);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }

        classroom.remove(new Student(idStudent));
        classroomRepository.save(classroom);
        return ResponseEntity.ok().build();
    }
}
