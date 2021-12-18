package com.itech.showtimeAPI.admin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
public class Admin {

    @Id
    private String id;
    private String name;

    @Indexed(unique = true)
    private String email;

    private String passWord;

    public Admin(String name, String email, String passWord) {
        this.name = name;
        this.email = email;
        this.passWord = passWord;
    }
}
