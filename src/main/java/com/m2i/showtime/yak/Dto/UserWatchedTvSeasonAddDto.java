package com.m2i.showtime.yak.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWatchedTvSeasonAddDto {
    String userMail ;
    Long tvTmdbId;
    Long tvSeasonid;
}
