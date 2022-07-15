package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import static com.m2i.showtime.yak.Security.Password.PasswordEncoder.encodePassword;

@Entity
@Table(name = "_user")
@Getter
@Setter
@NoArgsConstructor
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
    private String backgroundPicture;
    @Column(name = "_password")
    private String password;
    private String country;
    private LocalDateTime dateCreated = LocalDateTime.now();
    private LocalDate dateLastConnection;
    //-------------------------------------------------
    private Boolean isDeleted = false;
    private Boolean isActive = false;
    private Boolean isNotificationsActive = false;
    private Boolean isNotificationsTrophiesActive = false;
    private Boolean isNotificationsCommentsActive = false;
    private Boolean isAccountPrivate = false;
    //-------------------------------------------------
    private Duration totalMovieWatchedTime = Duration.ZERO;
    private Long totalMovieWatchedNumber = 0L;
    private Duration totalSeriesWatchedTime = Duration.ZERO;
    private Long totalSeriesWatchedNumber = 0L;
    private Long totalEpisodesWatchedNumber = 0L;
    //-------------------------------------------------

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_watched_movies",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Movie> watchedMovies = new HashSet<>();

    @OneToMany
    private Set<Role> roles = new HashSet<>();

    public User(String firstName,
                String lastName,
                String email,
                String password,
                String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = encodePassword(password);
        this.country = country;
    }

}
