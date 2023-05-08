package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchSingleMovieApiDto {
    private String title;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private String release_date;
    private Integer  runtime;
    private String vote_average;
    private String vote_count;
    private String id;
}
