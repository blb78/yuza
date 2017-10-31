package com.skillogs.yuza.net.http;


import com.skillogs.yuza.domain.User;
import com.skillogs.yuza.net.exception.ApiConflictException;

import com.skillogs.yuza.net.exception.ApiNotFoundException;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(UserController.URI)
public class UserController {
    public static final String URI = "/users";

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public User createUser(@RequestBody User user)  {

        if (repository.countByEmail(user.getEmail())>0) {
            throw new ApiConflictException();
        }

        return repository.save(user);
    }
    @PutMapping
    public User updateUser(@RequestBody User user){
        return repository.save(user);
    }
    @GetMapping
    public Page<User> findAll(Pageable pageable){
        return repository.findAll(pageable);
    }

    @PostMapping("/authenticate")
    public User authenticate(@RequestBody UserCredentials user){
        return repository.findByEmailAndPassword(user.getEmail(),user.getPassword());
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable String id)  {
        User user = repository.findById(id);
        if (user == null) throw new ApiNotFoundException();

        return user;
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
