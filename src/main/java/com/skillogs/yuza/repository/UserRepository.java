package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.*;


import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository  extends MongoRepository<User,Long> {

    User findById(String id);
    User findByEmail(String email);
    User findByEmailAndPassword(String email,String password);


}