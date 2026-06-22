package com.mySpringBootApplication.journalApp.controller;

import com.mySpringBootApplication.journalApp.entity.User;
import com.mySpringBootApplication.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Pubilc")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String healthCheck(){
        return "ok";
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody User newUser){
        try{
            userService.saveEntry(newUser);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(newUser, HttpStatus.BAD_REQUEST);
        }
    }
}


/*
* package com.mySpringBootApplication.journalApp.controller;

import com.mySpringBootApplication.journalApp.JournalAppApplication;
import com.mySpringBootApplication.journalApp.entity.JournalEntry;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/_Journal")
public class JournalEntryController {

    private Map<Long, JournalEntry> journalEntries = new HashMap<>();

    @GetMapping()
    public List<JournalEntry> getaAll(){
        return new ArrayList<>(journalEntries.values());
    }

    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry myEntry){
        journalEntries.put(myEntry.getId(), myEntry);
        return true;
    }

    @GetMapping("id/{id}")
    public JournalEntry findJournalById(@PathVariable Long id){
        return journalEntries.get(id);
    }

    @PutMapping("/id/{id}")
    public JournalEntry updateJournalById(@PathVariable Long id, @RequestBody JournalEntry myEntry) {
        return journalEntries.put(id, myEntry);
    }

    @DeleteMapping("/delete/{id}")
    public JournalEntry deleteJournalById(@PathVariable Long id) {
        return journalEntries.remove(id);
    }
}

* */