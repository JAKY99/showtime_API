package com.m2i.showtime.yak.Entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
@Getter
@Setter
public class Movie {

    @Id
    private Long id;

    private String name;

    public Movie() {
    }

    public Movie(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
