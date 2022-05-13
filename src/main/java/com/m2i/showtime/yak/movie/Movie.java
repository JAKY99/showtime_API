package com.m2i.showtime.yak.movie;


import com.m2i.showtime.yak.user.User;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Data
public class Movie {

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

    @Column(name = "name")
    private String name;

    public Movie() {
    }

    public Movie(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Movie(String name) {
        this.name = name;
    }
}
