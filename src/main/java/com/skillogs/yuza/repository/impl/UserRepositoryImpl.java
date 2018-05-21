package com.skillogs.yuza.repository.impl;

import com.skillogs.yuza.domain.user.Student;
import com.skillogs.yuza.domain.user.Teacher;
import com.skillogs.yuza.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @Override
    public void delete(Student student) {
        mgo.remove(student);
    }

    @Override
    public void delete(Teacher teacher) {
        mgo.remove(teacher);
    }

    @Override
    public Teacher findOneTeacher(String id) {
        return mgo.findOne(Query.query(Criteria.where("id").is(id)), Teacher.class);
    }

    @Override
    public Student findOneStudent(String id) {
        return mgo.findOne(Query.query(Criteria.where("id").is(id)), Student.class);
    }
}
