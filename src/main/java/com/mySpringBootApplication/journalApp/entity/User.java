package com.mySpringBootApplication.journalApp.entity;

import lombok.Data;

import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "user_entries")
@Data // has getters setters etc etc. Lambok
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String userName;
    @NonNull
    private String password;

    @DBRef // type of foreign key. we are creating reference of journal entry in user
    //meaning this list would keep a reference of journal entry
    private List<JournalEntry> journalEntryList = new ArrayList<>();

    private List<String> roles;

}
