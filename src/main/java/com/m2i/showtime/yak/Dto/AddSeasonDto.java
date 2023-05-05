package com.m2i.showtime.yak.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddSeasonDto {
    public AddEpisodeDto[] episodes;
    public Long id;
    public String name;
    public Long season_number;
    public String air_date;

}
