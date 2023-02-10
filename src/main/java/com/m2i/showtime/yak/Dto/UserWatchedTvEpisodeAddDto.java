package com.m2i.showtime.yak.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserWatchedTvEpisodeAddDto {
    String userMail ;
    Long tvTmdbId;
    Long tvSeasonid;
    Long episodeId;
    String serieName;
}