package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class createSeasonIfNotExistDto {
    private String username;
    private long episodeNumber;
    private long seasonNumber;
    private long tvTmdbId;
    private long episodeTmdbId;
    private long seasonTmdbId;
}
