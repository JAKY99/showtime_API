package com.itech.showtimeAPI.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
public abstract class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    @Field
    @Encrypted
    private String passWord;

    private LocalDateTime dob;
    private String country;

    private String role;

    public User(String firstName,
                String lastName,
                String email,
                String passWord,
                LocalDateTime dob,
                String country,
                String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passWord = passWord;
        this.dob = LocalDateTime.now();
        this.country = country;
        this.role = role;
    }

}
