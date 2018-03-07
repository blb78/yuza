package com.skillogs.yuza.net.http.user;

import com.skillogs.yuza.domain.account.Account;
import com.skillogs.yuza.domain.user.Cursus;
import com.skillogs.yuza.domain.user.Promotion;
import com.skillogs.yuza.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(TeacherController.URI)
public class TeacherController {
    public static final String URI = "/teachers";

    private final PromotionRepository promotionRepository;

    @Autowired
    public TeacherController(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @GetMapping("/{id}/cursus")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<Set<Cursus>> getFollowedCursus(@PathVariable String id) {
        return ResponseEntity.ok(promotionRepository.findByTeacherId(id).stream()
                .map(Promotion::getCursus)
                .collect(Collectors.toSet()));
    }

    @GetMapping("/me/cursus")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Set<Cursus>> getFollowedCourses(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(promotionRepository.findByTeacherId(account.getId()).stream()
                .map(Promotion::getCursus)
                .collect(Collectors.toSet()));
    }
}
