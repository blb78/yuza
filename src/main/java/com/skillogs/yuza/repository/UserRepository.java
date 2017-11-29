package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;

public interface UserRepository {
    Teacher save(Teacher teacher );
    Student save(Student student );
}
