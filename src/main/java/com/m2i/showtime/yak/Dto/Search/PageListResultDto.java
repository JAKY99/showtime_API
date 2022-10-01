package com.m2i.showtime.yak.Dto.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageListResultDto {
    private List listOfResults;
    private long totalRecords;
}
