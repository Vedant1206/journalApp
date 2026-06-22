package com.mySpringBootApplication.journalApp.service;

import com.mySpringBootApplication.journalApp.entity.JournalEntry;
import com.mySpringBootApplication.journalApp.entity.User;
import com.mySpringBootApplication.journalApp.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Component //This will make it into a bean
public class JournalEntryService

// controller --> service --> repository(entity) --> DB
//
{
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName){
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepository.save(journalEntry);

        User currentUser = userService.findByUserName(userName);
        currentUser.getJournalEntryList().add(saved);

        userService.saveEntry(currentUser);
    }
    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findUsingId(String id){
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public void deleteById(String id, String userName){
        User currentUser = userService.findByUserName(userName);
        currentUser.getJournalEntryList().removeIf(x-> x.getId().equals((id)));
        userService.saveEntry(currentUser);
        journalEntryRepository.deleteById(id);
    }
}
