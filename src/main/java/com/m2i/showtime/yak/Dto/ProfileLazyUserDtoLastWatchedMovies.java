package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Entity.Movie;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProfileLazyUserDtoLastWatchedMovies {
    private long[] lastWatchedMovies = new long[0];
    private long[] favoritesMovies = new long[0];
    private long[] watchlistMovies = new long[0];
}
