package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.user.Classroom;

import java.util.Set;

public interface ClassroomRepository {
    Classroom create(Classroom classroom);
    void delete(Classroom classroom);
    Classroom findOne(String id);

    Classroom save(Classroom classroom);

    Set<Classroom> findByStudentId(String idStudent);
}
