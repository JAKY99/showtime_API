package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class TheMovieDbApiMovieDto {
    int[] genre_ids;
    int id;
    String overview;
    String title;
    String poster_path;
    String backdrop_path;
    String release_date;
}
