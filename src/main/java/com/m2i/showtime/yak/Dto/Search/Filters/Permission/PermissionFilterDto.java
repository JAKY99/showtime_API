package com.m2i.showtime.yak.Dto.Search.Filters.Permission;

import com.m2i.showtime.yak.Dto.Search.Filters.FilterDefaultDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PermissionFilterDto {
    FilterDefaultDto displayName;
    FilterDto permissions;
    FilterDefaultDto description;
}
