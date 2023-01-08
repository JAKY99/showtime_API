package com.m2i.showtime.yak.Entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_watched_movies")
@Getter
@Setter
@NoArgsConstructor
public class UsersWatchedMovie {
    @EmbeddedId
    private UsersWatchedMovieId id;

    @MapsId("movieId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "watched_number")
    private Long watchedNumber;
    @Column(name = "watched_date")
    private LocalDateTime watchedDate = LocalDateTime.now();


}