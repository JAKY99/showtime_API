package com.m2i.showtime.yak.Dto;

import lombok.Getter;

@Getter
public class InsertMovieBulkElasticDto {
    int userId;
    int [] movieIds;
}
