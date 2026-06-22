package com.mySpringBootApplication.journalApp.repository;

import com.mySpringBootApplication.journalApp.entity.JournalEntry;
import com.mySpringBootApplication.journalApp.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUserName(String userName);
    void deleteByUserName(String username);
}
