package com.m2i.showtime.yak.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class Actor {
    @Id
    @SequenceGenerator(
            name = "movie_sequence",
            sequenceName = "movie_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "movie_sequence"
    )
    Long id;
    @Column(unique = true, nullable = false)
    Long TmdbId;

    public Actor() {
    }

    public Actor(Long TmdbId){
        this.TmdbId = TmdbId;
    }
}
