package com.skillogs.yuza.net.http.user;


import com.skillogs.yuza.domain.User;
import com.skillogs.yuza.net.dto.UserDto;
import com.skillogs.yuza.net.dto.UserMapper;
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

import java.util.Optional;


@RestController
@RequestMapping(UserController.URI)
public class UserController {

    public static final String URI = "/users";

    private final UserRepository repository;
    private final UserMapper mapper;
    private final Validator<UserDto> validator;

    @Autowired
    public UserController(UserRepository repository,
                          UserMapper mapper,
                          Validator<UserDto> validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    public Page<UserDto> findAll(Pageable pageable){
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserDto create(@RequestBody UserDto user)  {
        validator.validate(user);
        return mapper.toDTO(repository.save(mapper.to(user)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserDto> findOne(@PathVariable String id)  {
        return Optional.ofNullable(repository.findById(id))
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> findMe(@AuthenticationPrincipal User authenticated)  {
        return Optional.ofNullable(repository.findById(authenticated.getId()))
                .map(mapper::toDTO)
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

        validator.validate(user);
        return Optional.ofNullable(repository.save(mapper.to(user)))
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
