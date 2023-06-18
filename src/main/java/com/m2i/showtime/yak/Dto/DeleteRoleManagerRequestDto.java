package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRoleManagerRequestDto {
    private Long id;
    private String role;
    private String display_name;
    private String description;
    private String permissions;
}
