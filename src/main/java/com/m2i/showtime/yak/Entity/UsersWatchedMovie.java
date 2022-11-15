package com.m2i.showtime.yak.Entity;

import javax.persistence.*;

@Entity
@Table(name = "users_watched_movies")
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

    public UsersWatchedMovieId getId() {
        return id;
    }

    public void setId(UsersWatchedMovieId id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getWatchedNumber() {
        return watchedNumber;
    }

    public void setWatchedNumber(Long watchedNumber) {
        this.watchedNumber = watchedNumber;
    }

}