package com.skillogs.yuza.net.http;


import com.skillogs.yuza.domain.User;
import com.skillogs.yuza.net.exception.ApiConflictException;

import com.skillogs.yuza.net.exception.ApiCourseNotFoundException;
import com.skillogs.yuza.net.exception.ApiNotFoundException;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping(UserController.URI)
public class UserController {
    public static final String URI = "/users";

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<User> findAll(Pageable pageable){
        return repository.findAll(pageable);
    }

    @PostMapping
    public User createUser(@RequestBody User user)  {

        if (repository.countByEmail(user.getEmail())>0) {
            throw new ApiConflictException();
        }

        return repository.save(user);
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> findUser(@PathVariable String id)  {
        return Optional.ofNullable(repository.findById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user){
        user.setId(id);
        User currentUser = repository.findById(id);

        if (currentUser == null){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return Optional.ofNullable(repository.save(user))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void  deleteUser(@PathVariable String id)  {
        User user = repository.findById(id);
        if (user == null){
            throw new ApiNotFoundException();
        }
        repository.delete(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<User> authenticate(@RequestBody UserCredentials user){
        return Optional.ofNullable(repository.findByEmailAndPassword(user.getEmail(),user.getPassword()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<Set<String>> findCourses(@PathVariable String id)  {
        return Optional.ofNullable(repository.findById(id))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}/courses")
    public ResponseEntity<Set<String>> deleteAllCourses(@PathVariable String id)  {
        User user = repository.findById(id);
        if (user == null){
            throw new ApiNotFoundException();
        }
        Set<String> s = Collections.emptySet();
        user.setCourses(s);
        return Optional.ofNullable(repository.save(user))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}/courses/{course}")
    public ResponseEntity<Set<String>> addCourse(@PathVariable String id, @PathVariable String course)  {
        User user = repository.findById(id);
        if (user == null){
            throw new ApiNotFoundException();
        }
        user.addCourse(course);

        return Optional.ofNullable(repository.save(user))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
    @DeleteMapping("/{id}/courses/{course}")
    public ResponseEntity<Set<String>> deleteCourse(@PathVariable String id, @PathVariable String course)  {
        User user = repository.findById(id);
        if (user == null){
            throw new ApiNotFoundException();
        }
        Set hSet = user.getCourses();
        if (!hSet.contains(new String(course))){
            throw new ApiCourseNotFoundException();
        }

        hSet.remove(course);
        user.setCourses(hSet);
        return Optional.ofNullable(repository.save(user))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public static class UserCredentials {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


}
