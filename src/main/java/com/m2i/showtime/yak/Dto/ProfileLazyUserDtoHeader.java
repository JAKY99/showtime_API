package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileLazyUserDtoHeader {
    private long numberOfWatchedMovies;
    private long numberOfWatchedSeries;
    private String totalTimeWatchedSeries;
    private String totalTimeWatchedMovies;

}
