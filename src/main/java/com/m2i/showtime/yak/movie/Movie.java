package com.m2i.showtime.yak.movie;


import com.m2i.showtime.yak.user.User;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_watched_movies",
            joinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<User> users = new HashSet<>();

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
