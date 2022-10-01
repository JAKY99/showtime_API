package com.m2i.showtime.yak.Dto.Search;

import com.m2i.showtime.yak.Dto.Search.Filters.Permission.PermissionFilterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchParamsDto {

    private int first;
    private int limitRow;
    private SortingDto sort;
    private PermissionFilterDto filters;
}
