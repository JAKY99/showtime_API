package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TheMovieDbApiMovieDto {
    int[] genre_ids;
    int id;
    String overview;
    String title;
}
