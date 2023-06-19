package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Entity.Season;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWatchedTvSeasonAddV2Dto {
    String userMail ;
    Long tvTmdbId;
    Season season;
}
