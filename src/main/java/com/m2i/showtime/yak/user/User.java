package com.m2i.showtime.yak.user;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "_user")
@Data
public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @Column(name = "_password")
    private String password;

    private LocalDate created;
    private String country;

    public User() {
    }

    public User(Long id,
                String firstName,
                String lastName,
                String email,
                String password,
                String country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.created = LocalDate.now();
        this.country = country;
    }

    public User(String firstName,
                String lastName,
                String email,
                String password,
                String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.created = LocalDate.now();
        this.country = country;
    }

}
