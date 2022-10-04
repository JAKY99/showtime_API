package com.m2i.showtime.yak.Entity;

import javax.persistence.*;

@Entity
@Table(name = "serie")
public class Serie {
    @Id
    private Long id;

    private Long imdb_id;

    private String name;

    public Serie() {
    }

    public Serie(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}