package com.skillogs.yuza.repository.impl;

import com.skillogs.yuza.domain.Course;
import com.skillogs.yuza.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CourseRepositoryImpl implements CourseRepository {

    private final MongoOperations mgo;

    @Autowired
    public CourseRepositoryImpl(MongoOperations mgo) {
        this.mgo = mgo;
    }

    @Override
    public Course findOne(String id) {
        return mgo.findOne(Query.query(Criteria.where("id").is(id)), Course.class);
    }

    @Override
    public Course create(Course course) {
        mgo.save(course);
        return course;
    }
}
