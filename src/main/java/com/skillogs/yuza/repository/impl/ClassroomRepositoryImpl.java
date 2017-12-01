package com.skillogs.yuza.repository.impl;

import com.google.common.collect.Sets;
import com.skillogs.yuza.domain.user.Classroom;
import com.skillogs.yuza.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class ClassroomRepositoryImpl implements ClassroomRepository {

    private final MongoOperations mgo;

    @Autowired
    public ClassroomRepositoryImpl(MongoOperations mgo) {
        this.mgo = mgo;
    }

    @Override
    public Classroom create(Classroom classroom) {
        mgo.save(classroom);
        return classroom;
    }

    @Override
    public void delete(Classroom classroom) {
        mgo.remove(classroom);
    }

    @Override
    public Classroom findOne(String id) {
        return mgo.findOne(Query.query(Criteria.where("id").is(id)), Classroom.class);
    }

    @Override
    public Classroom save(Classroom classroom) {
        mgo.save(classroom);
        return classroom;
    }

    @Override
    public Set<Classroom> findByStudentId(String idStudent) {
        return Sets.newHashSet(mgo.find(Query.query(Criteria.where("students._id").is(idStudent)), Classroom.class));
    }
}
