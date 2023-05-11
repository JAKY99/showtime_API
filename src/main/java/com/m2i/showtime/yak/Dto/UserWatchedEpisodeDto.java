package com.m2i.showtime.yak.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWatchedEpisodeDto {
    String userMail ;
    Long episodeTmdbId;
}
