package com.skillogs.yuza.net.http.account;


import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.domain.account.Account;
import com.skillogs.yuza.net.dto.AccountDto;
import com.skillogs.yuza.net.dto.AccountMapper;
import com.skillogs.yuza.net.validator.Validator;
import com.skillogs.yuza.repository.AccountRepository;
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
@RequestMapping(AccountController.URI)
public class AccountController {

    public static final String URI = "/accounts";

    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final Validator<AccountDto> validator;
    private final UserRepository userRepository;

    @Autowired
    public AccountController(AccountRepository repository,
                             AccountMapper mapper,
                             Validator<AccountDto> validator,
                             UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    public Page<AccountDto> findAll(Pageable pageable){
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public AccountDto create(@RequestBody AccountDto account)  {
        validator.validate(account);

        Account savedAccount = repository.save(mapper.to(account));

        saveUser(savedAccount);
        return mapper.toDTO(savedAccount);
    }

    private void saveUser(Account account) {
        switch (account.getRole()) {
            case INSTRUCTOR:
                userRepository.save(new Teacher(account.getId()));
                break;
            case STUDENT:
                userRepository.save(new Student(account.getId()));
                break;
                default:
                    throw new RuntimeException("Cannot create User for Role "+ account.getRole());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<AccountDto> findOne(@PathVariable String id)  {
        return Optional.ofNullable(repository.findById(id))
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDto> findMe(@AuthenticationPrincipal Account authenticated) {
        return Optional.ofNullable(repository.findById(authenticated.getId()))
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Account> update(@PathVariable String id, @RequestBody AccountDto account){
        account.setId(id);
        Account currentAccount = repository.findById(id);
        if (currentAccount == null){
            return ResponseEntity.notFound().build();
        }
        account.setPassword(currentAccount.getPassword());

        validator.validate(account);
        return Optional.ofNullable(repository.save(mapper.to(account)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity delete(@PathVariable String id)  {
        Account account = repository.findById(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        repository.delete(account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Account> authenticate(@RequestBody AccountCredentials account){
        return Optional.ofNullable(repository.findByEmailAndPassword(account.getEmail(),account.getPassword()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }


    public static class AccountCredentials {
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
