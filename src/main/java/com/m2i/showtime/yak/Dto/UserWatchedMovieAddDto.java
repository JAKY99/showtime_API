package com.m2i.showtime.yak.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserWatchedMovieAddDto {
    String userMail ;
    Long tmdbId;
    String movieName;
}
