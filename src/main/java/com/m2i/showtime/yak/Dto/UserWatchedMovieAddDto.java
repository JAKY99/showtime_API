package com.m2i.showtime.yak.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWatchedMovieAddDto {
    String userMail ;
    Long tmdbId;
    String movieName;
}
