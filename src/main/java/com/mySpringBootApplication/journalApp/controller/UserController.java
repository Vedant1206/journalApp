package com.mySpringBootApplication.journalApp.controller;

import com.mySpringBootApplication.journalApp.entity.JournalEntry;
import com.mySpringBootApplication.journalApp.entity.User;
import com.mySpringBootApplication.journalApp.repository.UserRepository;
import com.mySpringBootApplication.journalApp.service.JournalEntryService;
import com.mySpringBootApplication.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User existingUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User checkUser = userService.findByUserName(userName);

        checkUser.setUserName(existingUser.getUserName());
        checkUser.setPassword(existingUser.getPassword());
        userService.saveEntry(checkUser);
        return new ResponseEntity<>(checkUser, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<User> deleteUserById(@RequestBody User existingUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUserName(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
