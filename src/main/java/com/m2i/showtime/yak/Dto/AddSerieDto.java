package com.m2i.showtime.yak.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddSerieDto {
    public int id;

    public String name;
    public AddSeasonDto[] seasons;
    public int number_of_episodes;
    public int number_of_seasons;

}
