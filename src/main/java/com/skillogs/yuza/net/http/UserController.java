package com.skillogs.yuza.net.http;


import com.skillogs.yuza.domain.User;
import com.skillogs.yuza.net.dto.UserDto;
import com.skillogs.yuza.net.dto.UserMapper;
import com.skillogs.yuza.net.validator.impl.UserValidator;
import com.skillogs.yuza.net.validator.Validator;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping(UserController.URI)
public class UserController {

    public static final String URI = "/users";

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final Validator<UserDto> userValidator;

    @Autowired
    public UserController(UserRepository repository,
                          UserMapper userMapper,
                          UserValidator userValidator) {
        this.repository = repository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    public Page<UserDto> findAll(Pageable pageable){
        return repository.findAll(pageable).map(userMapper::toDTO);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserDto create(@RequestBody UserDto user)  {
        userValidator.validate(user);
        return userMapper.toDTO(repository.save(userMapper.to(user)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserDto> findOne(@PathVariable String id)  {
        return Optional.ofNullable(repository.findById(id))
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> findMe(@AuthenticationPrincipal User authenticated)  {
        return Optional.ofNullable(repository.findById(authenticated.getId()))
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody UserDto user){
        user.setId(id);
        User currentUser = repository.findById(id);
        if (currentUser == null){
            return ResponseEntity.notFound().build();
        }
        user.setPassword(currentUser.getPassword());

        userValidator.validate(user);
        return Optional.ofNullable(repository.save(userMapper.to(user)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity delete(@PathVariable String id)  {
        User user = repository.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        repository.delete(user);
        return ResponseEntity.ok().build();
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
    public ResponseEntity unfollowAllCourses(@PathVariable String id)  {
        User user = repository.findById(id);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        user.setCourses(Collections.emptySet());
        return Optional.ofNullable(repository.save(user))
                .map(u -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/courses/{course}")
    public ResponseEntity<Set<String>> followCourse(@PathVariable String id, @PathVariable String course)  {
        User user = repository.findById(id);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        user.follow(course);

        return Optional.ofNullable(repository.save(user))
                .map(User::getCourses)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}/courses/{course}")
    public ResponseEntity<Set<String>> unfollowCourse(@PathVariable String id, @PathVariable String course)  {
        User user = repository.findById(id);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        if (!user.isFollowing(course)){
            return ResponseEntity.notFound().build();
        }

        user.unfollow(course);
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
