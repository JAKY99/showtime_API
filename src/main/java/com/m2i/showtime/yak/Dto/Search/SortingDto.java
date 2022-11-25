package com.m2i.showtime.yak.Dto.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SortingDto {

    private String sortField;
    private int sortOrder;
}
