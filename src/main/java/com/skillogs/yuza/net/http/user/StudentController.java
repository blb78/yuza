package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.domain.Course;
import com.skillogs.yuza.domain.user.Classroom;
import com.skillogs.yuza.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(StudentController.URI)
public class StudentController {
    public static final String URI = "/students";

    private final ClassroomRepository classroomRepository;

    @Autowired
    public StudentController(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Set<Course>> getFollowedCourses(@PathVariable String id) {
        return ResponseEntity.ok(classroomRepository.findByStudentId(id).stream()
                .map(Classroom::getCourses)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
    }
}
