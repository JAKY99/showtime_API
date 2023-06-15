package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRemoveSeasonDto {
    String username ;
    Long tvTmdbId;
    Long seasonTmdbId;
    Long seasonNumber;
}
