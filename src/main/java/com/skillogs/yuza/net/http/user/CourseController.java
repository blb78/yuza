//package com.skillogs.yuza.net.http.user;
//
//import com.skillogs.yuza.domain.account.Account;
//import com.skillogs.yuza.net.http.account.AccountController;
//import com.skillogs.yuza.repository.AccountRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//import java.util.Optional;
//import java.util.Set;
//
//@RestController
//@RequestMapping(CourseController.URI)
//public class CourseController {
//    public static final String URI = AccountController.URI + "/{userId}/courses";
//
//    private final AccountRepository repository;
//
//    @Autowired
//    public CourseController(AccountRepository repository) {
//        this.repository = repository;
//    }
//
//    @GetMapping
//    public ResponseEntity<Set<String>> findCourses(@PathVariable String userId)  {
//        return Optional.ofNullable(repository.findById(userId))
//                .map(Account::getCourses)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping
//    public ResponseEntity unfollowAllCourses(@PathVariable String userId)  {
//        Account account = repository.findById(userId);
//        if (account == null){
//            return ResponseEntity.notFound().build();
//        }
//        account.setCourses(Collections.emptySet());
//        return Optional.ofNullable(repository.save(account))
//                .map(u -> ResponseEntity.ok().build())
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PutMapping("/{courseId}")
//    public ResponseEntity<Set<String>> followCourse(@PathVariable String userId, @PathVariable String courseId)  {
//        Account account = repository.findById(userId);
//        if (account == null){
//            return ResponseEntity.notFound().build();
//        }
//        account.follow(courseId);
//
//        return Optional.ofNullable(repository.save(account))
//                .map(Account::getCourses)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{courseId}")
//    public ResponseEntity<Set<String>> unfollowCourse(@PathVariable String userId, @PathVariable String courseId)  {
//        Account account = repository.findById(userId);
//        if (account == null){
//            return ResponseEntity.notFound().build();
//        }
//        if (!account.isFollowing(courseId)){
//            return ResponseEntity.notFound().build();
//        }
//
//        account.unfollow(courseId);
//        return Optional.ofNullable(repository.save(account))
//                .map(Account::getCourses)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//}
