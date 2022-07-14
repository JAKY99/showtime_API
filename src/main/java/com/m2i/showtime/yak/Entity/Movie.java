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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_watched_movies",
            joinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "movie_has_categories",
            joinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "category_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    @EqualsAndHashCode.Exclude
    private Set<Category> categories = new HashSet<>();

    public Movie() {
    }

    public Movie(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
