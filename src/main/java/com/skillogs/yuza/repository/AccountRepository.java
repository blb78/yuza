package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.account.Account;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AccountRepository extends MongoRepository<Account,String> {

    long countByEmail(String email);
    Account findById(String id);
    Account findByEmailAndPassword(String email, String password);

}