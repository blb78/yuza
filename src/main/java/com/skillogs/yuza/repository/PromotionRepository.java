package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.user.Promotion;

import java.util.Set;

public interface PromotionRepository {
    Promotion create(Promotion promotion);
    void delete(Promotion promotion);
    Promotion findOne(String id);

    Promotion save(Promotion promotion);

    Set<Promotion> findByStudentId(String idStudent);


    Set<Promotion> findAll();
    Set<Promotion> findAll(String idTeacher);
}
