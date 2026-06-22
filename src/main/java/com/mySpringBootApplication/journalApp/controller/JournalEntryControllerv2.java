package com.mySpringBootApplication.journalApp.controller;

import com.mySpringBootApplication.journalApp.entity.JournalEntry;
import com.mySpringBootApplication.journalApp.entity.User;
import com.mySpringBootApplication.journalApp.service.JournalEntryService;
import com.mySpringBootApplication.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Journal")
public class JournalEntryControllerv2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("/{userName}")
    public ResponseEntity<?> getaAllJournalByUserName(@PathVariable String userName){
        User currentUser = userService.findByUserName(userName);
        if(currentUser != null){
            List<JournalEntry> entry = currentUser.getJournalEntryList();
            if(entry != null && !entry.isEmpty()){
                return new ResponseEntity<>(entry, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PostMapping("/{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName){
        try{

            journalEntryService.saveEntry(myEntry, userName);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(myEntry, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{id}")
    public ResponseEntity<JournalEntry> findJournalById(@PathVariable String id){
        Optional<JournalEntry> entry = journalEntryService.findUsingId(id);
        if ( entry.isPresent()){
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/delete/{userName}/{id}")
    public ResponseEntity<?> deleteJournalById(@PathVariable String id, @PathVariable String userName) {
        journalEntryService.deleteById(id, userName);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{userName}/{id}")
    public ResponseEntity<JournalEntry> updateJournalById(@PathVariable String id, @RequestBody JournalEntry newEntry, @PathVariable String userName) {
        Optional<JournalEntry> optional = journalEntryService.findUsingId(id);
        if (optional.isPresent()) {
            JournalEntry old = optional.get();
            old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : old.getTitle());
            old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : old.getContent());
            journalEntryService.saveEntry(old);
            return new ResponseEntity<>(old, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
