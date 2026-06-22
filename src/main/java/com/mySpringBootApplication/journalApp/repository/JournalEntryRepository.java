package com.mySpringBootApplication.journalApp.repository;

import com.mySpringBootApplication.journalApp.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {

}
