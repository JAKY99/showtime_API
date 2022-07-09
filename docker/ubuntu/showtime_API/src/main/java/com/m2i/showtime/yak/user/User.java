package com.m2i.showtime.yak.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m2i.showtime.yak.movie.Movie;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    @Transient
    private String fullName;
    private String email;
    @Column(name = "profile_picture", length = 64)
    private String profilePicture;

    @Column(name = "_password")
    private String password;

    private String country;
    private LocalDate dateCreated;
    private Boolean deleted = false;
    private Long nbWatchedMovies;
    private Long nbWatchedTvShows;
    private Float totalMovieWatchedTime;
    private Float totalTvShowsWatchedTime;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_watched_movies",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Movie> watchedMovies = new HashSet<>();

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
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.dateCreated = LocalDate.now();
    }

    public User(String firstName,
                String lastName,
                String email,
                String password,
                String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.dateCreated = LocalDate.now();
    }

}
