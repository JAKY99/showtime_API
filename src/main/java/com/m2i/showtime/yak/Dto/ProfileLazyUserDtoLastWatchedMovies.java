package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileLazyUserDtoLastWatchedMovies {
    private long[] lastWatchedMovies = new long[0];
    private long[] favoritesMovies = new long[0];
    private long[] watchlistMovies = new long[0];
    private int totalWatchedMovies = 0;
    private int totalFavoritesMovies = 0;
    private int totalWatchlistMovies = 0;
}
