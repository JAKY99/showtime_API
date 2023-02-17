package com.m2i.showtime.yak.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserWatchedSerieAddDto {
    String userMail ;
    Long tmdbId;
}
