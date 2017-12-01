package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.user.Classroom;

public interface ClassroomRepository {
    Classroom create(Classroom classroom);
    void delete(Classroom classroom);
    Classroom findOne(String id);

    Classroom save(Classroom classroom);
}
