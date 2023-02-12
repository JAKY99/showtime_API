package com.m2i.showtime.yak.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserWatchedEpisodeDto {
    String userMail ;
    Long serieTmdbId;
    Long seasonNumber;
    Long episodeNumber;
}
