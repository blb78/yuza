package com.skillogs.yuza.repository.impl;

import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final MongoOperations mgo;

    @Autowired
    public UserRepositoryImpl(MongoOperations mgo) {
        this.mgo = mgo;
    }

    @Override
    public Teacher save(Teacher teacher) {
        mgo.save(teacher);
        return teacher;
    }

    @Override
    public Student save(Student student) {
        mgo.save(student);
        return student;
    }
}
