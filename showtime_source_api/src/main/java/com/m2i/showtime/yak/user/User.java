package com.m2i.showtime.yak.user;

import com.m2i.showtime.yak.movie.Movie;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "_user")
@Getter
@Setter
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
    @Column(unique = true)
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
        this.password = encodePassword(password);
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
        this.password = encodePassword(password);
        this.country = country;
        this.dateCreated = LocalDate.now();
    }

    @Bean
    private String encodePassword(String password){

        int saltLength = 16; // salt length in bytes
        int hashLength = 32; // hash length in bytes
        int parallelism = 1; // currently not supported by Spring Security
        int memory = 4096;   // memory costs
        int iterations = 3;

        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(
                saltLength,
                hashLength,
                parallelism,
                memory,
                iterations);

        return argon2PasswordEncoder.encode(password);
    }

}
