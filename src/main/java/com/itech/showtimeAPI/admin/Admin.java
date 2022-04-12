package com.itech.showtimeAPI.admin;

import com.itech.showtimeAPI.user.User;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user")
@TypeAlias("admin")
public class Admin extends User {

    public Admin(String firstName,
                 String lastName,
                 String email,
                 String passWord,
                 LocalDateTime dob,
                 String country) {
        super(firstName, lastName, email, passWord, dob, country, "admin");
    }
}
