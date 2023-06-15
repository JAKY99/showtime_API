package com.m2i.showtime.yak.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRemoveEpisodeDto {
    String username ;
    Long tvTmdbId;
    Long seasonTmdbId;
    Long episodeTmdbId;
    Long seasonNumber;
    Long episodeNumber;
}
