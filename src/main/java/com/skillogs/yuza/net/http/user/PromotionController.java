package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.domain.account.Account;
import com.skillogs.yuza.domain.user.Promotion;
import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.repository.PromotionRepository;
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
@RequestMapping(PromotionController.URI)
public class PromotionController {
    public static final String URI = "/promotions";

    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;

    @Autowired
    public PromotionController(PromotionRepository promotionRepository,
                               UserRepository userRepository) {
        this.promotionRepository = promotionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    public ResponseEntity<Set<Promotion>> findAll(@AuthenticationPrincipal Account authenticated){
        switch (authenticated.getRole()){
            case ADMIN:
                return ResponseEntity.ok(promotionRepository.findAll());
            case TEACHER:
                return ResponseEntity.ok(promotionRepository.findAll(authenticated.getId()));
            default:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public Promotion create(@RequestBody Promotion promotion) {
        return promotionRepository.create(promotion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        if (promotionRepository.findOne(id) == null) {
            return ResponseEntity.notFound().build();
        }
        promotionRepository.delete(new Promotion(id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getOne(@PathVariable String id) {
        return Optional.ofNullable(promotionRepository.findOne(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/teachers/{idTeacher}")
    public ResponseEntity addTeacher(@PathVariable String id, @PathVariable String idTeacher) {
        Promotion promotion = promotionRepository.findOne(id);
        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }

        Teacher teacher = userRepository.findOneTeacher(idTeacher);
        if (teacher == null) {
            return ResponseEntity.notFound().build();
        }

        promotion.add(teacher);
        promotionRepository.save(promotion);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/students/{idStudent}")
    public ResponseEntity addStudent(@PathVariable String id, @PathVariable String idStudent) {
        Promotion promotion = promotionRepository.findOne(id);
        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }

        Student student = userRepository.findOneStudent(idStudent);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        promotion.add(student);
        promotionRepository.save(promotion);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}/teachers/{idTeacher}")
    public ResponseEntity deleteTeacher(@PathVariable String id, @PathVariable String idTeacher) {
        return deleteFromPromotion(id, promotion -> promotion.remove(new Teacher(idTeacher)));
    }

    @DeleteMapping("/{id}/students/{idStudent}")
    public ResponseEntity deleteStudent(@PathVariable String id, @PathVariable String idStudent) {
        return deleteFromPromotion(id, promotion -> promotion.remove(new Student(idStudent)));
    }

    private ResponseEntity deleteFromPromotion(String id, Consumer<Promotion> cons) {
        Promotion promotion = promotionRepository.findOne(id);
        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }
        cons.accept(promotion);
        promotionRepository.save(promotion);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/teachers")
    public ResponseEntity<Set<Teacher>> getTeachers(@PathVariable String id) {
        return retrieveFromPromotion(id, Promotion::getTeachers);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<Set<Student>> getStudents(@PathVariable String id) {
        return retrieveFromPromotion(id, Promotion::getStudents);
    }

    private <T> ResponseEntity<T> retrieveFromPromotion(@PathVariable String id, Function<Promotion, T> mapper) {
        return Optional.ofNullable(promotionRepository.findOne(id))
                .map(mapper)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
