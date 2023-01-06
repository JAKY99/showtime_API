package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "serie")
public class Serie {
    @Id
    private Long id;

    private Long tmdbId;

    private String name;

    public Serie() {
    }

    public Serie(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}