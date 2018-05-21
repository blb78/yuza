package com.skillogs.yuza.repository.impl;

import com.google.common.collect.Sets;
import com.skillogs.yuza.domain.user.Promotion;
import com.skillogs.yuza.repository.PromotionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class PromotionRepositoryImpl implements PromotionRepository {

    private final MongoOperations mgo;

    @Autowired
    public PromotionRepositoryImpl(MongoOperations mgo) {
        this.mgo = mgo;
    }

    @Override
    public Promotion create(Promotion promotion) {
        mgo.save(promotion);
        return promotion;
    }

    @Override
    public void delete(Promotion promotion) {
        mgo.remove(promotion);
    }

    @Override
    public Promotion findOne(String id) {
        return mgo.findOne(query(where("id").is(id)), Promotion.class);
    }

    @Override
    public Promotion save(Promotion promotion) {
        mgo.save(promotion);
        return promotion;
    }

    @Override
    public Set<Promotion> findByStudentId(String idStudent) {
        List<Promotion> elements = mgo.find(query(where("students.id").is(new ObjectId(idStudent))), Promotion.class);
        return Sets.newHashSet(elements);
    }

    @Override
    public Set<Promotion> findByTeacherId(String idTeacher) {
        List<Promotion> elements = mgo.find(query(where("teachers.id").is(new ObjectId(idTeacher))), Promotion.class);
        return Sets.newHashSet(elements);
    }

    @Override
    public Set<Promotion> findAll() {
        return Sets.newHashSet(mgo.findAll(Promotion.class));
    }

    @Override
    public Set<Promotion> findAll(String idTeacher) {
        return Sets.newHashSet(mgo.find(query(where("teachers._id").is(new ObjectId(idTeacher))), Promotion.class));
    }

}
