package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.domain.User;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(CourseController.URI)
public class CourseController {
    public static final String URI = UserController.URI + "/{userId}/courses";

    private final UserRepository repository;

    @Autowired
    public CourseController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Set<String>> findCourses(@PathVariable String userId)  {
        return Optional.ofNullable(repository.findById(userId))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity unfollowAllCourses(@PathVariable String userId)  {
        User user = repository.findById(userId);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        user.setCourses(Collections.emptySet());
        return Optional.ofNullable(repository.save(user))
                .map(u -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Set<String>> followCourse(@PathVariable String userId, @PathVariable String courseId)  {
        User user = repository.findById(userId);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        user.follow(courseId);

        return Optional.ofNullable(repository.save(user))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Set<String>> unfollowCourse(@PathVariable String userId, @PathVariable String courseId)  {
        User user = repository.findById(userId);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        if (!user.isFollowing(courseId)){
            return ResponseEntity.notFound().build();
        }

        user.unfollow(courseId);
        return Optional.ofNullable(repository.save(user))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
