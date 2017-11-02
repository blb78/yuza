package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.*;


import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository  extends MongoRepository<User,Long> {

    long countByEmail(String email);
    User findById(String id);
    User findByEmailAndPassword(String email,String password);



}