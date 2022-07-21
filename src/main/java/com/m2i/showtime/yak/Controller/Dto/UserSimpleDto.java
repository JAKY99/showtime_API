package com.m2i.showtime.yak.Controller.Dto;

import com.m2i.showtime.yak.Entity.Movie;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public interface UserSimpleDto {
    long getId();
    String getFirstName();
    String getLastName();
    String getFullName();
    String getProfilePicture();
    String getBackgroundPicture();
    String getCountry();
    LocalDateTime getDateCreated();
    Duration getTotalMovieWatchedTime();
    Long getTotalMovieWatchedNumber();
    Duration getTotalSeriesWatchedTime();
    Long getTotalSeriesWatchedNumber();
    Long getTotalEpisodesWatchedNumber();
    Set<Movie> getWatchedMovies();
}
