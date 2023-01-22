package com.m2i.showtime.yak.Entity;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "movie")
@Getter
@Setter
public class Movie {

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
    private Long id;

    private Long tmdbId;
    private String name;

    public Movie() {
    }

    public Movie(Long tmdbId, String name) {
        this.tmdbId = tmdbId;
        this.name = name;
    }
}
