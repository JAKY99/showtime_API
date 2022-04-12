package com.itech.showtimeAPI.consommer;

import com.itech.showtimeAPI.user.User;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
@TypeAlias("consumer")
public class Consumer extends User {

    protected Integer nbWatchedMovies;
    protected Integer nbWatchedTvShows;
    protected Double totalMovieWatchedTime;
    protected Double totalTvShowsWatchedTime;
    protected List<String> watchedMovies;
    protected List<String> watchedTvShows;
    protected List<String> favoriteMovies;
    protected List<String> favoriteTvShows;

    public Consumer(String firstName,
                    String lastName,
                    String email,
                    String passWord,
                    LocalDateTime dob,
                    String country) {
        super(firstName, lastName, email, passWord, dob, country, "consumer");
        this.nbWatchedMovies = 0;
        this.nbWatchedTvShows = 0;
        this.totalMovieWatchedTime = 0.0;
        this.totalTvShowsWatchedTime = 0.0;
        this.watchedMovies = new ArrayList<>();
        this.watchedTvShows = new ArrayList<>();
        this.favoriteMovies = new ArrayList<>();
        this.favoriteTvShows = new ArrayList<>();
    }
}
